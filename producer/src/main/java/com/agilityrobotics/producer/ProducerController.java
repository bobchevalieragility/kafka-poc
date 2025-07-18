package com.agilityrobotics.producer;

import com.agilityrobotics.models.arcevents.ArcEvent;
import com.agilityrobotics.models.arcevents.InterventionStart;
import com.agilityrobotics.models.arcevents.ShiftStart;
import com.google.protobuf.Timestamp;
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
    long millis = System.currentTimeMillis();
    Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
        .setNanos((int) ((millis % 1000) * 1000000)).build();
    ArcEvent msg = ArcEvent.newBuilder().setId("123").setEventTime(timestamp).setShiftStart(shiftEvent).build();
    this.eventPublisher.sendMessage("arc-events", msg);

    final InterventionStart interventionEvent = InterventionStart.newBuilder().setBar(val).build();
    millis = System.currentTimeMillis();
    timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
        .setNanos((int) ((millis % 1000) * 1000000)).build();
    msg = ArcEvent.newBuilder().setId("234").setEventTime(timestamp).setInterventionStart(interventionEvent).build();
    this.eventPublisher.sendMessage("arc-events", msg);
  }

}
