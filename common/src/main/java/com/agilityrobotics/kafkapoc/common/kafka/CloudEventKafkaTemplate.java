package com.agilityrobotics.kafkapoc.common.kafka;

import com.amazonaws.services.schemaregistry.common.SchemaByDefinitionFetcher;
import com.amazonaws.services.schemaregistry.serializers.protobuf.ProtobufSerializer;
import com.amazonaws.services.schemaregistry.utils.AWSSchemaRegistryConstants;
import com.google.protobuf.Any;
import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import io.cloudevents.v1.proto.CloudEvent;
import io.cloudevents.v1.proto.CloudEvent.CloudEventAttributeValue;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;
import software.amazon.awssdk.services.glue.model.DataFormat;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 */
public class CloudEventKafkaTemplate extends KafkaTemplate<String, CloudEvent> {
  private final SchemaByDefinitionFetcher schemaFetcher;
  private final ProtobufSerializer protobufSerializer;

  public CloudEventKafkaTemplate(final ProducerFactory<String, CloudEvent> producerFactory,
      final SchemaByDefinitionFetcher schemaFetcher, final ProtobufSerializer protobufSerializer) {
    super(producerFactory);
    this.schemaFetcher = schemaFetcher;
    this.protobufSerializer = protobufSerializer;
  }

  public CompletableFuture<SendResult<String, CloudEvent>> sendEvent(
      final String source,
      final String topic,
      final Message message) {
    String fullName = message.getDescriptorForType().getFullName();
    UUID schemaVersionId = registerSchema(message, fullName);

    CloudEvent event = CloudEvent.newBuilder()
        .setId(UUID.randomUUID().toString())
        .putAttributes("time", getTimestampAttribute())
        .putAttributes("dataschema", getDataSchemaAttribute(schemaVersionId))
        .setType(fullName)
        .setProtoData(Any.pack(message))
        .setSource(source)
        .build();

    // TODO generate partition key from org and facility
    return super.send(topic, "fake-key", event);
  }

  private UUID registerSchema(final Message message, final String fullName) {
    String schema = this.protobufSerializer.getSchemaDefinition(message);
    Map<String, String> metadata = Map.of(AWSSchemaRegistryConstants.TRANSPORT_METADATA_KEY, fullName);
    return this.schemaFetcher.getORRegisterSchemaVersionId(schema, fullName, DataFormat.PROTOBUF.name(), metadata);
  }

  private CloudEventAttributeValue getTimestampAttribute() {
    long millis = System.currentTimeMillis();
    Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
        .setNanos((int) ((millis % 1000) * 1000000)).build();
    return CloudEventAttributeValue.newBuilder().setCeTimestamp(timestamp).build();
  }

  private CloudEventAttributeValue getDataSchemaAttribute(UUID schemaVersionId) {
    return CloudEventAttributeValue.newBuilder().setCeString(schemaVersionId.toString()).build();
  }

}
