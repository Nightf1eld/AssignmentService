package ru.tcb.assignmentservice.screen.employee;

import io.jmix.core.DataManager;
import io.jmix.core.SaveContext;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.Button;
import io.jmix.ui.model.DataLoader;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tcb.assignmentservice.domain.entities.Employee;
import ru.tcb.assignmentservice.domain.entities.SiebelEmployee;

import java.util.List;

@UiController("Employee.browse")
@UiDescriptor("employee-browse.xml")
@LookupComponent("employeesTable")
public class EmployeeBrowse extends StandardLookup<Employee> {
    @Autowired
    private DataManager dm;
    @Autowired
    private DataLoader employeesDl;

    @Subscribe("employeesTable.synchronise")
    public void onEmployeesTableSynchronise(Action.ActionPerformedEvent event) {
        SaveContext context = new SaveContext().setDiscardSaved(true);
        List<Employee> employees = dm.load(Employee.class).all().list();
        List<SiebelEmployee> siebelEmplyees = dm.load(SiebelEmployee.class).all().list();
        for(Employee emp :employees){
            if(siebelEmplyees.stream().noneMatch(i->i.getUsername().equalsIgnoreCase(emp.getUsername()))) {
                emp.setActive(false);
                context.saving(emp);
            }
        }
        for(SiebelEmployee emp:siebelEmplyees){
            if(employees.stream().noneMatch(i->i.getUsername().equalsIgnoreCase(emp.getUsername()))){
                Employee employee = dm.create(Employee.class);
                employee.setActive(true);
                employee.setUsername(emp.getUsername());
                employee.setIntegrationId(emp.getId());
                employee.setFullName(emp.getFullName());
                context.saving(employee);
            }
        }
        dm.save(context);
        employeesDl.load();
    }

}