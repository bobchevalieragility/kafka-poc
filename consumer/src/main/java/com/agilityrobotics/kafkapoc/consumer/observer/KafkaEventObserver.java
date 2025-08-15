package com.agilityrobotics.kafkapoc.consumer.observer;

import com.agilityrobotics.kafkapoc.consumer.service.EventConsumerService;
import com.agilityrobotics.models.events.ArcEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventObserver {

  @Autowired
  private EventConsumerService eventConsumerService;

  @KafkaListener(topics = "${arc.kafka.arcevents.topic}", containerFactory = "arcEventListenerContainerFactory", groupId = "${spring.kafka.consumer.group-id}")
  public void listen(ArcEvent event) {
    eventConsumerService.processEvent(event);
  }

}
