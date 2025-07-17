package com.agilityrobotics.producer;

import com.agilityrobotics.models.arcevents.workcell.ShiftStart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
// @CrossOrigin
public class ProducerController {

  @Autowired
  private final EventPublisher eventPublisher;

  public ProducerController(EventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  @PostMapping("/publish")
  public void updateAvailability(@RequestBody String val) {
    final ShiftStart msg = ShiftStart.newBuilder().setFoo(val).build();
    this.eventPublisher.sendMessage("arc-events", msg);
  }

}
