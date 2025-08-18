package com.agilityrobotics.kafkapoc.common.config;

import com.agilityrobotics.kafkapoc.common.kafka.CloudEventKafkaTemplate;
import io.cloudevents.core.message.Encoding;
import io.cloudevents.kafka.CloudEventSerializer;
import io.cloudevents.protobuf.ProtobufFormat;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;

import java.util.Map;

@Configuration
@EnableKafka
public class CloudEventKafkaProducerConfig {

  @Bean
  public CloudEventKafkaTemplate cloudEventKafkaTemplate(final KafkaProperties kafkaProperties) {
    final Map<String, Object> props = kafkaProperties.buildProducerProperties(null);
    // props.put(CloudEventSerializer.ENCODING_CONFIG, Encoding.BINARY.toString());
    props.put(CloudEventSerializer.ENCODING_CONFIG, Encoding.STRUCTURED.toString());
    props.put(CloudEventSerializer.EVENT_FORMAT_CONFIG, ProtobufFormat.PROTO_CONTENT_TYPE.toString());

    return new CloudEventKafkaTemplate(new DefaultKafkaProducerFactory<>(props));
  }

}
