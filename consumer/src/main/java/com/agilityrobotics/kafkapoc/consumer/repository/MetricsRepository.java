package com.agilityrobotics.kafkapoc.consumer.repository;

import com.agilityrobotics.kafkapoc.models.arcevents.ArcEvent;

public interface MetricsRepository {

  public void createEvent(ArcEvent event);

}
