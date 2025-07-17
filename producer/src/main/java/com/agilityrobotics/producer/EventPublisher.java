package com.agilityrobotics.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {

  @Autowired
  private final KafkaTemplate<String, com.google.protobuf.Message> kafkaTemplate;

  EventPublisher(final KafkaTemplate<String, com.google.protobuf.Message> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendMessage(final String topic, final com.google.protobuf.Message msg) {
    this.kafkaTemplate.send(topic, msg);
  }

}
