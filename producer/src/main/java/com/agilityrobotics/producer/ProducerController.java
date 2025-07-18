package com.agilityrobotics.producer;

import com.agilityrobotics.models.arcevents.workcell.InterventionStart;
import com.agilityrobotics.models.arcevents.workcell.ShiftStart;
import com.google.protobuf.DynamicMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerController {

  @Autowired
  private final EventPublisher eventPublisher;

  public ProducerController(EventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  @PostMapping("/publish")
  public void updateAvailability(@RequestBody String val) {
    // Publish two different events to the same Kafka topic
    final ShiftStart shiftEvent = ShiftStart.newBuilder().setFoo(val).build();
    DynamicMessage msg = DynamicMessage.newBuilder(shiftEvent).build();
    this.eventPublisher.sendMessage("arc-events", msg);

    final InterventionStart interventionEvent = InterventionStart.newBuilder().setBar(val).build();
    msg = DynamicMessage.newBuilder(interventionEvent).build();
    this.eventPublisher.sendMessage("arc-events", msg);
  }

}
