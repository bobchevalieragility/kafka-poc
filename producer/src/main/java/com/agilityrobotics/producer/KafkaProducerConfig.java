package com.agilityrobotics.producer;

import com.amazonaws.services.schemaregistry.utils.AWSSchemaRegistryConstants;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import software.amazon.awssdk.services.glue.model.Compatibility;
import software.amazon.awssdk.services.glue.model.DataFormat;

import java.util.Map;

@Configuration
@EnableKafka
// @RequiredArgsConstructor
public class KafkaProducerConfig {

  @Bean
  public KafkaTemplate<String, com.google.protobuf.Message> kafkaTemplate(
      KafkaProperties kafkaProperties) {
    Map<String, Object> props = kafkaProperties.buildProducerProperties(null);
    props.put(AWSSchemaRegistryConstants.DATA_FORMAT, DataFormat.PROTOBUF.name());
    props.put(AWSSchemaRegistryConstants.AWS_ENDPOINT, "http://moto-server:3000");
    props.put(AWSSchemaRegistryConstants.AWS_REGION, "us-west-2");
    // props.put(AWSSchemaRegistryConstants.REGISTRY_NAME, "my-registry");
    props.put(AWSSchemaRegistryConstants.SCHEMA_AUTO_REGISTRATION_SETTING, "true");
    // props.put(AWSSchemaRegistryConstants.SCHEMA_NAME, "protobuf-file-name.proto")
    // props.put(AWSSchemaRegistryConstants.SCHEMA_NAME, "my-schema"); // If not
    // passed, uses transport name (topic name in
    // // case of Kafka, or stream name in case of Kinesis
    // // Data Streams)
    // props.put(AWSSchemaRegistryConstants.REGISTRY_NAME, "my-registry"); // If not
    // passed, uses "default-registry"
    // props.put(AWSSchemaRegistryConstants.CACHE_TIME_TO_LIVE_MILLIS, "86400000");
    // // If not passed, uses 86400000 (24
    // // Hours)
    // props.put(AWSSchemaRegistryConstants.CACHE_SIZE, "10"); // default value is
    // 200
    props.put(AWSSchemaRegistryConstants.COMPATIBILITY_SETTING, Compatibility.FULL); // Pass a compatibility mode. If
                                                                                     // not passed, uses
                                                                                     // Compatibility.BACKWARD
    // props.put(AWSSchemaRegistryConstants.DESCRIPTION, "This registry is used for
    // several purposes."); // If not passed,
    // // constructs a
    // // description
    // props.put(AWSSchemaRegistryConstants.COMPRESSION_TYPE,
    // AWSSchemaRegistryConstants.COMPRESSION.ZLIB); // If not
    // passed,
    // records are
    // sent
    // uncompressed
    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
  }
}
