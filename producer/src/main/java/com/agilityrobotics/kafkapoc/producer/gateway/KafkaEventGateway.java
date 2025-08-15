package com.agilityrobotics.kafkapoc.producer.gateway;

import com.agilityrobotics.models.events.InterventionStarted;
import com.agilityrobotics.models.events.ShiftStarted;
import io.cloudevents.v1.proto.CloudEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventGateway implements EventGateway {

  @Autowired
  private KafkaTemplate<String, CloudEvent> eventProducer;

  @Value("${arc.kafka.arcevents.topic}")
  private String topic;

  @Override
  public void emitShiftStartEvent(final ShiftStarted shiftStarted) {
    // Publish two different events to the same Kafka topic
    // ArcEvent event =
    // ArcEvent.newBuilder().setId("123").setEventTime(getTimestamp()).setShiftStarted(shiftStarted)
    // .build();
    // this.eventProducer.send(this.topic, "fake-key", event);
  }

  @Override
  public void emitInterventionStartEvent(final InterventionStarted interventionStarted) {
    // ArcEvent event =
    // ArcEvent.newBuilder().setId("234").setEventTime(getTimestamp())
    // .setInterventionStarted(interventionStarted)
    // .build();
    // this.eventProducer.send(this.topic, "fake-key", event);
  }

}
