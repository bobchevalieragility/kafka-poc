package com.agilityrobotics.kafkapoc.common.kafka;

import com.agilityrobotics.kafkapoc.models.arcevents.ArcEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ArcEventProducer {

  @Autowired
  private final KafkaTemplate<String, ArcEvent> arcEventKafkaTemplate;

  ArcEventProducer(final KafkaTemplate<String, ArcEvent> arcEventKafkaTemplate) {
    this.arcEventKafkaTemplate = arcEventKafkaTemplate;
  }

  public void publish(final String topic, final ArcEvent event) {
    // TODO extract meaningful key from event
    this.arcEventKafkaTemplate.send(topic, "fake-key", event);
  }

}
