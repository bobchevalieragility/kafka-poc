package com.agilityrobotics.kafkapoc.producer.service;

import com.agilityrobotics.models.events.ArcEvent;
import com.agilityrobotics.models.events.InterventionStarted;
import com.agilityrobotics.models.events.ShiftStarted;
import com.google.protobuf.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventProducerService {

  @Autowired
  private KafkaTemplate<String, ArcEvent> eventProducer;

  @Value("${arc.kafka.arcevents.topic}")
  private String topic;

  public void emitShiftStartedEvent(final ShiftStarted shiftStarted) {
    // Publish two different events to the same Kafka topic
    ArcEvent event = ArcEvent.newBuilder().setId("123").setEventTime(getTimestamp()).setShiftStarted(shiftStarted)
        .build();
    this.eventProducer.send(this.topic, "fake-key", event);
  }

  public void emitInterventionStartedEvent(final InterventionStarted interventionStarted) {
    ArcEvent event = ArcEvent.newBuilder().setId("234").setEventTime(getTimestamp())
        .setInterventionStarted(interventionStarted)
        .build();
    this.eventProducer.send(this.topic, "fake-key", event);
  }

  private Timestamp getTimestamp() {
    long millis = System.currentTimeMillis();
    return Timestamp.newBuilder().setSeconds(millis / 1000)
        .setNanos((int) ((millis % 1000) * 1000000)).build();
  }

}
