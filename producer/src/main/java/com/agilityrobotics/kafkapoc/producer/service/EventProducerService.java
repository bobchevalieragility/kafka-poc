package com.agilityrobotics.kafkapoc.producer.service;

import com.agilityrobotics.kafkapoc.common.kafka.CloudEventKafkaTemplate;
import com.agilityrobotics.models.events.Organization;
import com.google.protobuf.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EventProducerService {

  @Autowired
  private CloudEventKafkaTemplate cloudEventKafkaTemplate;

  @Value("${arc.kafka.arcevents.topic}")
  private String topic;

  public void emitCloudEvent(final Organization org, final Message message) {
    this.cloudEventKafkaTemplate.sendEvent("kafka-poc-producer", this.topic, org, message);
  }

}
