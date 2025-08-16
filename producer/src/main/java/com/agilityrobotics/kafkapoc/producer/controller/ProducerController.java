package com.agilityrobotics.kafkapoc.producer.controller;

import com.agilityrobotics.kafkapoc.producer.service.EventProducerService;
import com.agilityrobotics.models.events.InterventionCategory;
import com.agilityrobotics.models.events.InterventionReason;
import com.agilityrobotics.models.events.InterventionStarted;
import com.agilityrobotics.models.events.Organization;
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
    Organization org = Organization.newBuilder().setId("org123").setFacilityId("fac123").build();
    Shift shift = Shift.newBuilder().setId("shift123").setWorkcellId("wc123").build();

    final ShiftStarted shiftEvent = ShiftStarted.newBuilder()
        .setOrg(org)
        .setShift(shift)
        .build();
    final InterventionStarted interventionEvent = InterventionStarted.newBuilder()
        .setOrg(org)
        .setShift(shift)
        .setCategory(InterventionCategory.newBuilder().setId("cat123").setName("CAT01").build())
        .setReason(InterventionReason.newBuilder().setId("reason123").setName("REASON01").build())
        .build();

    // Publish two different event types to the same Kafka topic
    eventProducerService.emitCloudEvent(org, shiftEvent);
    eventProducerService.emitCloudEvent(org, interventionEvent);
  }

}
