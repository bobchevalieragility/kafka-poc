package com.agilityrobotics.kafkapoc.producer.service;

import com.agilityrobotics.models.events.InterventionStarted;
import com.agilityrobotics.models.events.ShiftStarted;
import com.google.protobuf.Any;
import com.google.protobuf.Timestamp;
import io.cloudevents.v1.proto.CloudEvent;
import io.cloudevents.v1.proto.CloudEvent.CloudEventAttributeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventProducerService {

  @Autowired
  private KafkaTemplate<String, CloudEvent> eventProducer;

  @Value("${arc.kafka.arcevents.topic}")
  private String topic;

  public void emitShiftStartedEvent(final ShiftStarted shiftStarted) {
    String fullName = shiftStarted.getDescriptorForType().getFullName();
    CloudEvent event = wrapEvent(Any.pack(shiftStarted), fullName);
    this.eventProducer.send(this.topic, "fake-key", event);
  }

  public void emitInterventionStartedEvent(final InterventionStarted interventionStarted) {
    String fullName = interventionStarted.getDescriptorForType().getFullName();
    CloudEvent event = wrapEvent(Any.pack(interventionStarted), fullName);
    this.eventProducer.send(this.topic, "fake-key", event);
  }

  private CloudEvent wrapEvent(Any event, String fullName) {
    return CloudEvent.newBuilder()
        .setId("e1")
        .putAttributes("time", getTimestampAttribute())
        .putAttributes("dataschema", getDataSchemaAttribute(event))
        .setType(fullName)
        .setProtoData(event)
        .setSource("kafka-poc-producer")
        .build();
  }

  private CloudEventAttributeValue getTimestampAttribute() {
    long millis = System.currentTimeMillis();
    Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
        .setNanos((int) ((millis % 1000) * 1000000)).build();
    return CloudEventAttributeValue.newBuilder().setCeTimestamp(timestamp).build();
  }

  // TODO This should be a URL from the schema registry
  private CloudEventAttributeValue getDataSchemaAttribute(Any event) {
    return CloudEventAttributeValue.newBuilder().setCeString(event.getTypeUrl()).build();
  }

}
