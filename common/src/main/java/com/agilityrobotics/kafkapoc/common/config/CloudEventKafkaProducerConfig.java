package com.agilityrobotics.kafkapoc.common.config;

import com.agilityrobotics.kafkapoc.common.kafka.CloudEventKafkaTemplate;
import com.agilityrobotics.kafkapoc.common.properties.AwsSchemaRegistryProperties;
import com.amazonaws.services.schemaregistry.common.AWSSchemaRegistryClient;
import com.amazonaws.services.schemaregistry.common.SchemaByDefinitionFetcher;
import com.amazonaws.services.schemaregistry.common.configs.GlueSchemaRegistryConfiguration;
import com.amazonaws.services.schemaregistry.serializers.protobuf.ProtobufSerializer;
import io.cloudevents.v1.proto.CloudEvent;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

import java.util.Map;

@Configuration
@EnableKafka
public class CloudEventKafkaProducerConfig {

  @Bean
  public CloudEventKafkaTemplate cloudEventKafkaTemplate(
      final KafkaProperties kafkaProperties, final AwsSchemaRegistryProperties schemaRegistryProperties) {

    final Map<String, Object> registryProps = schemaRegistryProperties.buildProducerProperties();
    final Map<String, Object> factoryProps = kafkaProperties.buildProducerProperties(null);
    factoryProps.putAll(registryProps);
    final GlueSchemaRegistryConfiguration registryConfig = new GlueSchemaRegistryConfiguration(registryProps);

    final ProducerFactory<String, CloudEvent> producerFactory = new DefaultKafkaProducerFactory<>(factoryProps);
    final SchemaByDefinitionFetcher schemaFetcher = createSchemaFetcher(registryConfig);
    final ProtobufSerializer protobufSerializer = new ProtobufSerializer(registryConfig);

    return new CloudEventKafkaTemplate(producerFactory, schemaFetcher, protobufSerializer);
  }

  private SchemaByDefinitionFetcher createSchemaFetcher(final GlueSchemaRegistryConfiguration registryConfig) {
    DefaultCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();
    AWSSchemaRegistryClient client = new AWSSchemaRegistryClient(credentialsProvider, registryConfig);
    return new SchemaByDefinitionFetcher(client, registryConfig);
  }

}
