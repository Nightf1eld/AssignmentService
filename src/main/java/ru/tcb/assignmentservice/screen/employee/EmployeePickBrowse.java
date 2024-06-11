package ru.tcb.assignmentservice.screen.employee;

import io.jmix.ui.component.HasValue;
import io.jmix.ui.component.TextInputField;
import io.jmix.ui.model.DataLoader;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tcb.assignmentservice.domain.entities.Employee;

import java.util.Objects;

@UiController("EmployeePick.browse")
@UiDescriptor("employee-pick-browse.xml")
@LookupComponent("employeesTable")
public class EmployeePickBrowse extends StandardLookup<Employee> {
    @Autowired
    private DataLoader employeesDl;

    @Subscribe("searchField")
    public void onSearchFieldValueChange(HasValue.ValueChangeEvent<String> event) {
        if(event.getValue() != null && !event.getValue().isBlank()){
            employeesDl.setParameter("name", event.getValue());
        }
        else
            employeesDl.removeParameter("name");
        employeesDl.load();
    }
}