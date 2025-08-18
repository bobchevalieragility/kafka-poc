package com.agilityrobotics.kafkapoc.consumer.observer;

import com.agilityrobotics.kafkapoc.consumer.service.EventConsumerService;
import com.google.protobuf.InvalidProtocolBufferException;
import io.cloudevents.CloudEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventObserver {

  @Autowired
  private EventConsumerService eventConsumerService;

  @KafkaListener(topics = "${arc.kafka.arcevents.topic}", containerFactory = "cloudEventListenerContainerFactory", groupId = "${spring.kafka.consumer.group-id}")
  public void listen(@Headers MessageHeaders kafkaHeaders, CloudEvent event) throws InvalidProtocolBufferException {
    kafkaHeaders.forEach((k, v) -> System.out.println("Kafka header - " + k + ": " + v));
    eventConsumerService.processEvent(event);
  }

}
