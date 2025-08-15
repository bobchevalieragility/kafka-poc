package com.agilityrobotics.kafkapoc.consumer.service;

import com.agilityrobotics.kafkapoc.consumer.repository.MetricsRepository;
import com.agilityrobotics.models.events.InterventionStarted;
import com.agilityrobotics.models.events.ShiftStarted;
import com.google.protobuf.InvalidProtocolBufferException;
import io.cloudevents.v1.proto.CloudEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventConsumerService {

  @Autowired
  private MetricsRepository repository;

  public void processEvent(CloudEvent event) throws InvalidProtocolBufferException {
    switch (event.getType()) {
      case "events.ShiftStarted":
        ShiftStarted shiftStarted = event.getProtoData().unpack(ShiftStarted.class);
        System.out.println("Consumed a SHIFT_STARTED event: " + shiftStarted.toString());
        break;
      case "events.InterventionStarted":
        InterventionStarted interventionStarted = event.getProtoData().unpack(InterventionStarted.class);
        System.out.println("Consumed a INTERVENTION_STARTED event: " + event.toString());
        break;
      default:
        System.out.println("Unexpected type - this shouldn't happen: " + event.getType());
    }
    // repository.createEvent(event);
  }

}
