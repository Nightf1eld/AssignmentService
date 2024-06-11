package ru.tcb.assignmentservice.services;

import io.jmix.core.DataManager;
import io.jmix.core.SaveContext;
import io.jmix.core.security.SystemAuthenticator;
import io.jmix.ui.model.DataContext;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tcb.assignmentservice.domain.entities.Employee;
import ru.tcb.assignmentservice.domain.entities.Opportunity;
import ru.tcb.assignmentservice.domain.entities.Queue;
import ru.tcb.assignmentservice.entity.WorkLog;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class AssignmentService {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(AssignmentService.class);
    @Autowired
    private DataManager dataManager;

    @Autowired
    private SystemAuthenticator systemAuthenticator;

    public void assignManual(Set<Opportunity> opportunities, Queue queue, Employee employee) {
        SaveContext context = new SaveContext();
        opportunities.forEach(i -> {
            i.setLocked(false);
            i.setLockedDate(null);
            if (queue != null)
                i.setCurrentQueue(queue);
            i.setCurrentEmployee(employee);
            context.saving(i);
        });
        dataManager.save(context);
    }

    public void assignAutomatic(Opportunity opportunity, boolean uniResp) {
        systemAuthenticator.withSystem(() -> {
            List<Queue> queues = dataManager.load(Queue.class).all().list()
                    .stream()
                    .filter(Queue::getAllowAutomaticAssignment)
                    .sorted(Comparator.comparingInt(Queue::getSequence))
                    .toList();

            if (!uniResp) {
                opportunity.setCurrentEmployee(null);
                opportunity.setLockedDate(null);
                opportunity.setLocked(false);
            }

            boolean queueFound = false;
            for (Queue queue : queues) {
                if (queue.getCondition() == null)
                    continue;
                try {
                    if (dataManager.load(Opportunity.class)
                            .query("select o from Opportunity o where  o.id = :id and " + queue.getCondition())
                            .parameter("id", opportunity.getId())
                            .optional()
                            .orElse(null) != null) {
                        queueFound = true;

                        opportunity.setCurrentQueue(queue);
                        break;
                    }
                    ;
                } catch (Exception e) {
                    log.error("Error", e);
                }
            }

            if (!queueFound) {
                opportunity.setCurrentQueue(dataManager.load(Queue.class).query("select q from Queue q where q.allowAutomaticAssignment = true and q.condition is null").list()
                        .stream().min(Comparator.comparingInt(Queue::getSequence)).orElse(null));
            }

            dataManager.save(opportunity);
            return null;
        });
    }

    public String assign(Employee employee, Opportunity opportunity) {
        opportunity.setCurrentEmployee(employee);
        opportunity.setLocked(true);
        opportunity.setLockedDate(LocalDateTime.now());
        dataManager.save(opportunity);
        WorkLog workLog = dataManager.create(WorkLog.class);
        workLog.setOpportunity(opportunity);
        workLog.setEmployee(employee);
        workLog.setStartDate(LocalDateTime.now());
        workLog.setOpportunityIntegrationId(opportunity.getIntegrationId());
        dataManager.save(workLog);
        return opportunity.getIntegrationId();
    }
}
