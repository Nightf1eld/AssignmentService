package ru.tcb.assignmentservice.security;

import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.security.role.annotation.SpecificPolicy;
import io.jmix.securityui.role.annotation.MenuPolicy;
import io.jmix.securityui.role.annotation.ScreenPolicy;
import ru.tcb.assignmentservice.domain.entities.*;

@ResourceRole(name = "Manager", code = "manager", scope = "UI")
public interface ManagerRole {
    @MenuPolicy(menuIds = {"Employee.browse", "Opportunity.browse", "Queue.browse"})
    @ScreenPolicy(screenIds = {"Employee.browse", "Opportunity.browse", "Queue.browse", "EmployeePick.browse", "LoginScreen", "MainScreen", "Queue.edit", "bulkEditorWindow", "inputDialog", "notFoundScreen", "selectValueDialog", "Opportunity.edit"})
    void screens();

    @EntityAttributePolicy(entityClass = Employee.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Employee.class, actions = EntityPolicyAction.READ)
    void employee();

    @EntityAttributePolicy(entityClass = Opportunity.class, attributes = {"lockedDate", "locked", "currentEmployee", "currentQueue"}, action = EntityAttributePolicyAction.MODIFY)
    @EntityAttributePolicy(entityClass = Opportunity.class, attributes = {"id", "attributes", "integrationId", "name", "createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate", "deletedBy", "deletedDate"}, action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Opportunity.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void opportunity();

    @EntityAttributePolicy(entityClass = Queue.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Queue.class, actions = EntityPolicyAction.ALL)
    void queue();

    @EntityAttributePolicy(entityClass = SiebelEmployee.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = SiebelEmployee.class, actions = EntityPolicyAction.READ)
    void siebelEmployee();

    @EntityPolicy(entityClass = Attribute.class, actions = EntityPolicyAction.READ)
    @EntityAttributePolicy(entityClass = Attribute.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    void attribute();

    @SpecificPolicy(resources = {"ui.loginToUi", "ui.bulkEdit.enabled", "ui.presentations.global", "ui.showExceptionDetails"})
    void specific();
}