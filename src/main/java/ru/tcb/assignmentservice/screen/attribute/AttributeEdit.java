package ru.tcb.assignmentservice.screen.attribute;

import io.jmix.ui.screen.*;
import ru.tcb.assignmentservice.domain.entities.Attribute;

@UiController("Attribute.edit")
@UiDescriptor("attribute-edit.xml")
@EditedEntityContainer("attributeDc")
public class AttributeEdit extends StandardEditor<Attribute> {
}