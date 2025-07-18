package com.agilityrobotics.consumer;

import com.agilityrobotics.models.arcevents.ArcEvent;
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
  // public void listen(DynamicMessage msg) {
  // public void listen(WorkcellEvent msg) {
  public void listen(ArcEvent msg) {
    // if (msg.getDescriptorForType().getName() == "ShiftStart") {
    // }
    switch (msg.getEventCase()) {
      case SHIFT_START:
        System.out.println("Consumed a SHIFT_START event: foo=" + msg.getShiftStart().getFoo());
        break;
      case INTERVENTION_START:
        System.out.println("Consumed a INTERVENTION_START event: bar=" + msg.getInterventionStart().getBar());
        break;
      default:
        System.out.println("Unexpected type - this shouldn't happen");
    }
  }

}
