package com.agilityrobotics.kafkapoc.consumer.service;

import com.agilityrobotics.kafkapoc.consumer.repository.MetricsRepository;
import com.agilityrobotics.models.events.v1.InterventionStarted;
import com.agilityrobotics.models.events.v1.ShiftStarted;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import io.cloudevents.CloudEvent;
import io.cloudevents.protobuf.ProtoCloudEventData;
import io.cloudevents.protobuf.ProtobufFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.InputMismatchException;

@Service
public class EventConsumerService {

  @Autowired
  private MetricsRepository repository;

  public void processEvent(CloudEvent event) throws InvalidProtocolBufferException {
    if (!event.getDataContentType().equals(ProtobufFormat.PROTO_DATA_CONTENT_TYPE)) {
      String msg = String.format("Expected event datacontenty type to be %s but was %s",
          ProtobufFormat.PROTO_DATA_CONTENT_TYPE, event.getDataContentType());
      throw new InputMismatchException(msg);
    }

    String eventType = event.getType();
    OffsetDateTime eventTime = event.getTime();
    URI source = event.getSource();
    Any data = ((ProtoCloudEventData) event.getData()).getAny();
    System.out.println("Event Type: " + eventType);
    System.out.println("Event Time: " + eventTime);
    System.out.println("Event Source: " + source);

    if (eventType.equals(ShiftStarted.getDescriptor().getFullName())) {
      ShiftStarted shiftStarted = data.unpack(ShiftStarted.class);
      System.out.println(shiftStarted.toString());
    } else if (eventType.equals(InterventionStarted.getDescriptor().getFullName())) {
      InterventionStarted interventionStarted = data.unpack(InterventionStarted.class);
      System.out.println(interventionStarted.toString());
    } else {
      System.out.println("Unexpected type - this shouldn't happen: " + event.getType());
    }

    repository.createEvent(event);
  }

}
