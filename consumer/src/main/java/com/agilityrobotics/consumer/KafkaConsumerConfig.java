package com.agilityrobotics.consumer;

import com.amazonaws.services.schemaregistry.utils.AWSSchemaRegistryConstants;
import com.google.protobuf.DynamicMessage;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import software.amazon.awssdk.services.glue.model.Compatibility;
import software.amazon.awssdk.services.glue.model.DataFormat;

import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, DynamicMessage> kafkaListenerContainerFactory(
      KafkaProperties kafkaProperties) {
    ConcurrentKafkaListenerContainerFactory<String, DynamicMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(defaultConsumerFactory(kafkaProperties));
    return factory;
  }

  private ConsumerFactory<String, DynamicMessage> defaultConsumerFactory(KafkaProperties kafkaProperties) {
    Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);
    props.put(AWSSchemaRegistryConstants.DATA_FORMAT, DataFormat.PROTOBUF.name());
    props.put(AWSSchemaRegistryConstants.AWS_ENDPOINT, "http://moto-server:3000");
    props.put(AWSSchemaRegistryConstants.AWS_REGION, "us-west-2");
    // if REGISTRY_NAME is not set, schemas will be published to "default-registry"
    props.put(AWSSchemaRegistryConstants.REGISTRY_NAME, "arc-registry");
    props.put(AWSSchemaRegistryConstants.SCHEMA_AUTO_REGISTRATION_SETTING, "true");
    // Use a custom schema naming strategy to store schemas in the registry by
    // record name instead of by topic name. This allows us to publish multiple
    // schema types to the same topic.
    props.put(AWSSchemaRegistryConstants.SCHEMA_NAMING_GENERATION_CLASS,
        "com.agilityrobotics.models.protobuf.RecordSchemaNamingStrategy");
    props.put(AWSSchemaRegistryConstants.COMPATIBILITY_SETTING, Compatibility.FULL); // not passed, uses

    return new DefaultKafkaConsumerFactory<>(props);
  }
}
