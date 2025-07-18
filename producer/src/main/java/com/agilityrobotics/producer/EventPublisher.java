package com.agilityrobotics.producer;

import com.google.protobuf.DynamicMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {

  @Autowired
  private final KafkaTemplate<String, DynamicMessage> kafkaTemplate;

  EventPublisher(final KafkaTemplate<String, DynamicMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendMessage(final String topic, final DynamicMessage msg) {
    this.kafkaTemplate.send(topic, msg);
  }

}
