package com.agilityrobotics.kafkapoc.common.kafka;

import com.agilityrobotics.models.events.Organization;
import com.google.protobuf.Message;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.protobuf.ProtoCloudEventData;
import io.cloudevents.protobuf.ProtobufFormat;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CloudEventKafkaTemplate extends KafkaTemplate<String, CloudEvent> {

  public CloudEventKafkaTemplate(final ProducerFactory<String, CloudEvent> producerFactory) {
    super(producerFactory);
  }

  public CompletableFuture<SendResult<String, CloudEvent>> sendEvent(
      final String source,
      final String topic,
      final Organization org,
      final Message message) {

    String fullName = message.getDescriptorForType().getFullName();
    CloudEvent event = CloudEventBuilder.v1()
        .withId(UUID.randomUUID().toString())
        .withType(fullName)
        .withSource(URI.create(source))
        .withTime(OffsetDateTime.now())
        .withData(ProtoCloudEventData.wrap(message))
        .withDataContentType(ProtobufFormat.PROTO_DATA_CONTENT_TYPE)
        .build();

    String key = String.join("_", org.getId(), org.getFacilityId());
    return super.send(topic, key, event);
  }

}
