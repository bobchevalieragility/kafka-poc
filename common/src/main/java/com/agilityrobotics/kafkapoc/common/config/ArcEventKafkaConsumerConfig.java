package com.agilityrobotics.kafkapoc.common.config;

import com.agilityrobotics.kafkapoc.common.properties.AwsSchemaRegistryProperties;
import com.agilityrobotics.kafkapoc.models.arcevents.ArcEvent;
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
public class ArcEventKafkaConsumerConfig {

  @Bean
  public ConsumerFactory<String, ArcEvent> arcEventConsumerFactory(
      KafkaProperties kafkaProperties, AwsSchemaRegistryProperties schemaRegistryProperties) {
    Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);
    props.putAll(schemaRegistryProperties.buildConsumerProperties());
    props.put(AWSSchemaRegistryConstants.PROTOBUF_MESSAGE_TYPE, ProtobufMessageType.POJO.getName());
    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, ArcEvent> arcEventListenerContainerFactory(
      KafkaProperties kafkaProperties, AwsSchemaRegistryProperties schemaRegistryProperties) {
    ConcurrentKafkaListenerContainerFactory<String, ArcEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(arcEventConsumerFactory(kafkaProperties, schemaRegistryProperties));
    return factory;
  }

}
