package com.agilityrobotics.kafkapoc.producer.controller;

import com.agilityrobotics.kafkapoc.producer.service.EventProducerService;
import com.agilityrobotics.models.events.InterventionCategory;
import com.agilityrobotics.models.events.InterventionReason;
import com.agilityrobotics.models.events.InterventionStarted;
import com.agilityrobotics.models.events.Shift;
import com.agilityrobotics.models.events.ShiftStarted;
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
    final ShiftStarted shiftEvent = ShiftStarted.newBuilder()
        .setShift(Shift.newBuilder().setFacilityId("fac123").setId("shift123").build())
        .build();
    final InterventionStarted interventionEvent = InterventionStarted.newBuilder()
        .setCategory(InterventionCategory.newBuilder().setId("cat123").setName("CAT01").build())
        .setReason(InterventionReason.newBuilder().setId("reason123").setName("REASON01").build())
        .setShift(Shift.newBuilder().setFacilityId("fac123").setId("shift123").build())
        .build();

    // Publish two different event types to the same Kafka topic
    eventProducerService.emitCloudEvent(shiftEvent);
    eventProducerService.emitCloudEvent(interventionEvent);
  }

}
