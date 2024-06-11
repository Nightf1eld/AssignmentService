package ru.tcb.assignmentservice.screen.opportunity;

import io.jmix.ui.screen.*;
import ru.tcb.assignmentservice.domain.entities.Opportunity;

@UiController("Opportunity.edit")
@UiDescriptor("opportunity-edit.xml")
@EditedEntityContainer("opportunityDc")
public class OpportunityEdit extends StandardEditor<Opportunity> {
}