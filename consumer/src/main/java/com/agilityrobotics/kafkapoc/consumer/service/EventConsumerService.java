package com.agilityrobotics.kafkapoc.consumer.service;

import com.agilityrobotics.kafkapoc.consumer.repository.MetricsRepository;
import com.agilityrobotics.kafkapoc.models.arcevents.ArcEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventConsumerService {

  @Autowired
  private MetricsRepository repository;

  public void processEvent(ArcEvent event) {
    switch (event.getEventCase()) {
      case SHIFT_START:
        System.out.println("Consumed a SHIFT_START event: " + event.getShiftStart().toString());
        break;
      case INTERVENTION_START:
        System.out.println("Consumed a INTERVENTION_START event: " + event.toString());
        break;
      default:
        System.out.println("Unexpected type - this shouldn't happen");
    }
    repository.createEvent(event);
  }

}
