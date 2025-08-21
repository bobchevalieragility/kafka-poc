package com.agilityrobotics.kafkapoc.producer.controller;

import com.agilityrobotics.kafkapoc.producer.service.EventProducerService;
import com.agilityrobotics.models.events.v1.Facility;
import com.agilityrobotics.models.events.v1.InterventionCategory;
import com.agilityrobotics.models.events.v1.InterventionReason;
import com.agilityrobotics.models.events.v1.InterventionStarted;
import com.agilityrobotics.models.events.v1.Shift;
import com.agilityrobotics.models.events.v1.ShiftStarted;
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
    Facility facility = Facility.newBuilder().setId("fac123").setOrgId("org123").build();
    Shift shift = Shift.newBuilder().setId("shift123").setWorkcellId("wc123").build();

    final ShiftStarted shiftEvent = ShiftStarted.newBuilder()
        .setFacility(facility)
        .setShift(shift)
        .build();
    final InterventionStarted interventionEvent = InterventionStarted.newBuilder()
        .setFacility(facility)
        .setShift(shift)
        .setCategory(InterventionCategory.newBuilder().setId("cat123").setName("CAT01").build())
        .setReason(InterventionReason.newBuilder().setId("reason123").setName("REASON01").build())
        .build();

    // Publish two different event types to the same Kafka topic
    eventProducerService.emitCloudEvent(facility, shiftEvent);
    eventProducerService.emitCloudEvent(facility, interventionEvent);
  }

}
