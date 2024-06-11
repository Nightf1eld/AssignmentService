package ru.tcb.assignmentservice.screen.queue;

import io.jmix.ui.screen.*;
import ru.tcb.assignmentservice.domain.entities.Queue;

@UiController("Queue.browse")
@UiDescriptor("queue-browse.xml")
@LookupComponent("queuesTable")
public class QueueBrowse extends StandardLookup<Queue> {
}