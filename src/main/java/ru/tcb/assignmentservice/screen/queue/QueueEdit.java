package ru.tcb.assignmentservice.screen.queue;

import io.jmix.ui.screen.*;
import ru.tcb.assignmentservice.domain.entities.Queue;

@UiController("Queue.edit")
@UiDescriptor("queue-edit.xml")
@EditedEntityContainer("queueDc")
public class QueueEdit extends StandardEditor<Queue> {
}