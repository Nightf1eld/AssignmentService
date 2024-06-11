package ru.tcb.assignmentservice.screen.employee;

import io.jmix.ui.screen.*;
import ru.tcb.assignmentservice.domain.entities.Employee;

@UiController("Employee.edit")
@UiDescriptor("employee-edit.xml")
@EditedEntityContainer("employeeDc")
public class EmployeeEdit extends StandardEditor<Employee> {
}