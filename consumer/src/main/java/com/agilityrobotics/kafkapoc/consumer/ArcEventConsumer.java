package com.agilityrobotics.kafkapoc.consumer;

import com.agilityrobotics.kafkapoc.models.arcevents.ArcEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ArcEventConsumer {

  @KafkaListener(topics = "${arc.kafka.arcevents.topic}", containerFactory = "arcEventListenerContainerFactory", groupId = "${spring.kafka.consumer.group-id}")
  public void listen(ArcEvent msg) {
    switch (msg.getEventCase()) {
      case SHIFT_START:
        System.out.println("Consumed a SHIFT_START event: " + msg.getShiftStart().toString());
        break;
      case INTERVENTION_START:
        System.out.println("Consumed a INTERVENTION_START event: " + msg.toString());
        break;
      default:
        System.out.println("Unexpected type - this shouldn't happen");
    }
  }

}
