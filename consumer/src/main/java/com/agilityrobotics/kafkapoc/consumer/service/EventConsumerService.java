package com.agilityrobotics.kafkapoc.consumer.service;

import com.agilityrobotics.kafkapoc.consumer.repository.MetricsRepository;
import com.agilityrobotics.models.events.ArcEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventConsumerService {

  @Autowired
  private MetricsRepository repository;

  public void processEvent(ArcEvent event) {
    switch (event.getDataCase()) {
      case SHIFT_STARTED:
        System.out.println("Consumed a SHIFT_START event: " + event.getShiftStarted().toString());
        break;
      case INTERVENTION_STARTED:
        System.out.println("Consumed a INTERVENTION_START event: " + event.toString());
        break;
      default:
        System.out.println("Unexpected type - this shouldn't happen");
    }
    repository.createEvent(event);
  }

}
