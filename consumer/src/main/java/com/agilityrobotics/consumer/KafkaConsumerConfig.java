package com.agilityrobotics.consumer;

import com.agilityrobotics.models.arcevents.ArcEvent;
import com.amazonaws.services.schemaregistry.utils.AWSSchemaRegistryConstants;
import com.amazonaws.services.schemaregistry.utils.ProtobufMessageType;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, ArcEvent> kafkaListenerContainerFactory(
      KafkaProperties kafkaProperties, AwsSchemaRegistryProperties schemaRegistryProperties) {
    ConcurrentKafkaListenerContainerFactory<String, ArcEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(defaultConsumerFactory(kafkaProperties, schemaRegistryProperties));
    return factory;
  }

  private ConsumerFactory<String, ArcEvent> defaultConsumerFactory(KafkaProperties kafkaProperties,
      AwsSchemaRegistryProperties schemaRegistryProperties) {
    Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);
    props.putAll(schemaRegistryProperties.buildConsumerProperties());
    props.put(AWSSchemaRegistryConstants.PROTOBUF_MESSAGE_TYPE, ProtobufMessageType.POJO.getName());

    return new DefaultKafkaConsumerFactory<>(props);
  }
}
