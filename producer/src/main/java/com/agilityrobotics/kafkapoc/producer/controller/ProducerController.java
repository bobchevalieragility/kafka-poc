package com.agilityrobotics.kafkapoc.producer.controller;

import com.agilityrobotics.kafkapoc.models.arcevents.InterventionStart;
import com.agilityrobotics.kafkapoc.models.arcevents.ShiftStart;
import com.agilityrobotics.kafkapoc.producer.service.EventProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ProducerController {

  @Autowired
  private EventProducerService eventProducerService;

  @PostMapping("/publish")
  public void updateAvailability(@RequestBody Map<String, String> body) {
    // Publish two different events to the same Kafka topic
    eventProducerService.emitShiftStartEvent(ShiftStart.newBuilder().setFoo(body.get("val")).build());
    eventProducerService.emitInterventionStartEvent(InterventionStart.newBuilder().setBar(body.get("val")).build());
  }

}
