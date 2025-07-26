package com.agilityrobotics.kafkapoc.producer.gateway;

import com.agilityrobotics.kafkapoc.models.arcevents.ArcEvent;
import com.agilityrobotics.kafkapoc.models.arcevents.InterventionStart;
import com.agilityrobotics.kafkapoc.models.arcevents.ShiftStart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventGateway implements EventGateway {

  @Autowired
  private KafkaTemplate<String, ArcEvent> eventProducer;

  @Value("${arc.kafka.arcevents.topic}")
  private String topic;

  @Override
  public void emitShiftStartEvent(final ShiftStart shiftStart) {
    // Publish two different events to the same Kafka topic
    ArcEvent event = ArcEvent.newBuilder().setId("123").setEventTime(getTimestamp()).setShiftStart(shiftStart).build();
    this.eventProducer.send(this.topic, "fake-key", event);
  }

  @Override
  public void emitInterventionStartEvent(final InterventionStart interventionStart) {
    ArcEvent event = ArcEvent.newBuilder().setId("234").setEventTime(getTimestamp())
        .setInterventionStart(interventionStart)
        .build();
    this.eventProducer.send(this.topic, "fake-key", event);
  }

}
