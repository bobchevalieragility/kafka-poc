package com.agilityrobotics.kafkapoc.consumer.observer;

import com.agilityrobotics.kafkapoc.consumer.service.EventConsumerService;
import com.google.protobuf.InvalidProtocolBufferException;
import io.cloudevents.v1.proto.CloudEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;;

@Component
public class KafkaEventObserver {

  @Autowired
  private EventConsumerService eventConsumerService;

  @KafkaListener(topics = "${arc.kafka.arcevents.topic}", containerFactory = "arcEventListenerContainerFactory", groupId = "${spring.kafka.consumer.group-id}")
  public void listen(CloudEvent event) throws InvalidProtocolBufferException {
    eventConsumerService.processEvent(event);
  }

}
