package com.agilityrobotics.kafkapoc.producer.gateway;

import com.agilityrobotics.models.events.InterventionStarted;
import com.agilityrobotics.models.events.ShiftStarted;
import com.google.protobuf.Timestamp;

public interface EventGateway {

  public void emitShiftStartEvent(final ShiftStarted shiftStarted);

  public void emitInterventionStartEvent(final InterventionStarted interventionStarted);

  default Timestamp getTimestamp() {
    long millis = System.currentTimeMillis();
    return Timestamp.newBuilder().setSeconds(millis / 1000)
        .setNanos((int) ((millis % 1000) * 1000000)).build();
  }

}
