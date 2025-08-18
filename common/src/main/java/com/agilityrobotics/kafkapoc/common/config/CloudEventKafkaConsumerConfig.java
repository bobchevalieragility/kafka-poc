package com.agilityrobotics.kafkapoc.common.config;

import io.cloudevents.CloudEvent;
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
public class CloudEventKafkaConsumerConfig {

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, CloudEvent> cloudEventListenerContainerFactory(
      KafkaProperties kafkaProperties) {
    ConcurrentKafkaListenerContainerFactory<String, CloudEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(cloudEventConsumerFactory(kafkaProperties));
    return factory;
  }

  @Bean
  public ConsumerFactory<String, CloudEvent> cloudEventConsumerFactory(KafkaProperties kafkaProperties) {
    Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);
    return new DefaultKafkaConsumerFactory<>(props);
  }

}
