package com.agilityrobotics.consumer;

import com.google.protobuf.DynamicMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {

  // @Autowired
  // private final KafkaTemplate<String, com.google.protobuf.Message>
  // kafkaTemplate;

  // EventConsumer(final KafkaTemplate<String, com.google.protobuf.Message>
  // kafkaTemplate) {
  // this.kafkaTemplate = kafkaTemplate;
  // }

  @KafkaListener(topics = "arc-events", containerFactory = "kafkaListenerContainerFactory", groupId = "metrics-service")
  public void listen(DynamicMessage msg) {
    System.out.println("BFC consuming from topic - " + msg.toString());
  }

}
