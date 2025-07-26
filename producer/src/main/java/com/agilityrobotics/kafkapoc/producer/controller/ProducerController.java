package com.agilityrobotics.kafkapoc.producer.controller;

import com.agilityrobotics.kafkapoc.producer.service.ArcEventProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ProducerController {

  @Autowired
  private ArcEventProducerService eventProducerService;

  @PostMapping("/publish")
  public void updateAvailability(@RequestBody Map<String, String> body) {
    // Publish two different events to the same Kafka topic
    eventProducerService.emitShiftStartEvent(body.get("val"));
    eventProducerService.emitInterventionStartEvent(body.get("val"));
  }

}
