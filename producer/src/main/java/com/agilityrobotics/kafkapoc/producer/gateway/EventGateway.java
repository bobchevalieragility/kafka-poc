package com.agilityrobotics.kafkapoc.producer.gateway;

import com.agilityrobotics.kafkapoc.models.arcevents.InterventionStart;
import com.agilityrobotics.kafkapoc.models.arcevents.ShiftStart;
import com.google.protobuf.Timestamp;

public interface EventGateway {

  public void emitShiftStartEvent(final ShiftStart shiftStart);

  public void emitInterventionStartEvent(final InterventionStart interventionStart);

  default Timestamp getTimestamp() {
    long millis = System.currentTimeMillis();
    return Timestamp.newBuilder().setSeconds(millis / 1000)
        .setNanos((int) ((millis % 1000) * 1000000)).build();
  }

}
