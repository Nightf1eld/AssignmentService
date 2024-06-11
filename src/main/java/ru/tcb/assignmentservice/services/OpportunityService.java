package ru.tcb.assignmentservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jmix.core.DataManager;
import io.jmix.core.SaveContext;
import io.jmix.core.security.SystemAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tcb.assignmentservice.domain.entities.*;
import ru.tcb.assignmentservice.entity.WorkLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OpportunityService {
    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private DataManager dataManager;

    @Autowired
    private SystemAuthenticator systemAuthenticator;

    @Autowired
    private ObjectMapper mapper;

    public void register(String optyId, Map<String, String> data) {
        systemAuthenticator.withSystem(() -> {
            Opportunity opportunity = getOpportunityByIntegrationId(optyId);
            if (opportunity == null) {
                SaveContext context = new SaveContext().setDiscardSaved(true);
                opportunity = dataManager.create(Opportunity.class);
                opportunity.setIntegrationId(optyId);
                if (data.containsKey("name"))
                    opportunity.setName(data.get("name"));
                for (String key : data.keySet()) {
                    if (key.equals("name"))
                        continue;

                    Attribute attribute = dataManager.create(Attribute.class);
                    attribute.setOpportunity(opportunity);
                    attribute.setName(key);
                    if (data.get(key).matches("^[0-9 ]*[.,][0-9]+$"))
                        attribute.setValue(data.get(key).replaceAll(" |[.,][0-9]+$", ""));
                    else
                        attribute.setValue(data.get(key));
                    context.saving(attribute);
                }
                context.saving(opportunity);
                dataManager.save(context);
                assignmentService.assignAutomatic(opportunity, false);
            }
            return null;
        });
    }

    public void lock(String optyId, String employeeId) {
        systemAuthenticator.withSystem(() -> {
            Opportunity opportunity = getOpportunityByIntegrationId(optyId);
            Employee employee = getEmployeeByIntegrationId(employeeId);
            if (opportunity != null && employee != null) {
                if (!opportunity.getCurrentEmployee().equals(employee))
                    throw new IllegalStateException("Opportunity assigned employee changed");
                opportunity.setLockedDate(LocalDateTime.now());
                dataManager.save(opportunity);
            }
            return null;
        });
    }

    public String getOpportunity(String employeeId) {
        return systemAuthenticator.withSystem(() -> {

            Employee employee = getEmployeeByIntegrationId(employeeId);
            if (employee == null)
                return "";
            /* Pre assigned opportunities (queue availability ignored)*/
            log.info("checking pre assigned");
            List<Opportunity> opportunities = dataManager.load(Opportunity.class)
                    .query("select o from Opportunity o " +
                            "join Queue q where o.currentEmployee = :employee " +
                            "and o.currentEmployee = :employee and o.locked = false " +
                            "order by q.sequence, o.createdDate asc")
                    .parameter("employee", employee)
                    //.parameter("time", LocalDateTime.now().minus(LOCK_DURATION / 60, ChronoUnit.MINUTES))
                    .list();
            if (opportunities.size() > 0)
                return assignmentService.assign(employee, opportunities.get(0));
            log.info("Checking assigned queues");
            /* get all queues where employee can be assigned*/
            List<Queue> queues = dataManager.load(Queue.class).query("select q from Queue q " +
                            "left join q.employees e " +
                            "where (e = :employee or e.id is null)" +
                            "order by q.sequence")
                    .parameter("employee", employee)
                    .list();
            log.info("Checking common queues");
            /* find first suitable (not assigned) opportunity */
            for (Queue queue : queues) {
                List<Opportunity> queuedOpportunities = dataManager.load(Opportunity.class)
                        .query("select o from Opportunity o " +
                                "where o.currentQueue = :queue " +
                                "and o.currentEmployee is null and o.locked = false " +
                                "order by o.createdDate")
                        .parameter("queue", queue)
                        //.parameter("time", LocalDateTime.now().minus(LOCK_DURATION/60, ChronoUnit.MINUTES))
                        //.parameter("employee", employee)
                        .list();
                if (queuedOpportunities.size() > 0)
                    return assignmentService.assign(employee, queuedOpportunities.get(0));
            }
            return "";
        });
    }

    public void release(String optyId, boolean isDelete, String status) {
        systemAuthenticator.withSystem(() -> {
            SaveContext context = new SaveContext();
            Opportunity opportunity = getOpportunityByIntegrationId(optyId);

            if (opportunity != null) {
                Employee employee = opportunity.getCurrentEmployee();
                boolean uniResp = false;
                if (employee != null) {
                    SiebelEmployee siebelEmployee = getSiebelEmployeeByIntegrationId(employee.getIntegrationId());
                    uniResp = siebelEmployee.getUniResp();
                }

                if (!uniResp) {
                    dataManager.load(WorkLog.class)
                            .query("select w from WorkLog w where w.opportunity = :opportunity and w.employee = :employee and w.endDate is null")
                            .parameter("opportunity", opportunity)
                            .parameter("employee", opportunity.getCurrentEmployee())
                            .list()
                            .stream()
                            .forEach(w -> {
                                w.setEndDate(LocalDateTime.now());
                                context.saving(w);
                            });
                    opportunity.setLocked(false);
                    opportunity.setLockedDate(null);
                    opportunity.setCurrentEmployee(null);
                }

                Attribute attribute = opportunity
                        .getAttributes()
                        .stream()
                        .filter(i -> i.getName().equalsIgnoreCase("status"))
                        .findFirst()
                        .orElse(null);

                if (attribute != null && status != null && !status.isBlank()) {
                    attribute.setValue(status);
                    context.saving(attribute);
                }
                context.saving(opportunity);
                dataManager.save(context);

                if (isDelete)
                    dataManager.remove(opportunity);
                else
                    assignmentService.assignAutomatic(opportunity, uniResp);
            }
            return null;
        });
    }

    private Opportunity getOpportunityByIntegrationId(String integrationId) {
        return dataManager
                .load(Opportunity.class)
                .query("select o from Opportunity o where o.integrationId = :id")
                .parameter("id", integrationId)
                .optional()
                .orElse(null);
    }

    private Employee getEmployeeByIntegrationId(String integrationId) {
        return dataManager.load(Employee.class)
                .query("select e from Employee e where e.integrationId = :id and e.active = true")
                .parameter("id", integrationId)
                .optional()
                .orElse(null);
    }

    private SiebelEmployee getSiebelEmployeeByIntegrationId(String integrationId) {
        return dataManager.load(SiebelEmployee.class)
                .query("select s from SiebelEmployee s where s.id = :id")
                .parameter("id", integrationId)
                .optional()
                .orElse(null);
    }
}
