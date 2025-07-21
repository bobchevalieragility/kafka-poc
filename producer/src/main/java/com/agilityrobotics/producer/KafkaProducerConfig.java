package com.agilityrobotics.producer;

import com.agilityrobotics.models.arcevents.ArcEvent;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

@Configuration
@EnableKafka
public class KafkaProducerConfig {

  @Bean
  public KafkaTemplate<String, ArcEvent> kafkaTemplate(
      KafkaProperties kafkaProperties, AwsSchemaRegistryProperties schemaRegistryProperties) {
    Map<String, Object> props = kafkaProperties.buildProducerProperties(null);
    props.putAll(schemaRegistryProperties.buildProducerProperties());
    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
  }
}
