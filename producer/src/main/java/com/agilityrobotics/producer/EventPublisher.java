package com.agilityrobotics.producer;

import com.agilityrobotics.models.arcevents.ArcEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {

  @Autowired
  private final KafkaTemplate<String, ArcEvent> kafkaTemplate;

  EventPublisher(final KafkaTemplate<String, ArcEvent> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendMessage(final String topic, final ArcEvent msg) {
    this.kafkaTemplate.send(topic, "fake-key", msg);
  }

}
