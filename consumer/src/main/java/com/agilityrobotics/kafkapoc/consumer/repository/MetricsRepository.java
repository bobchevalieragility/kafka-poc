package com.agilityrobotics.kafkapoc.consumer.repository;

import com.agilityrobotics.kafkapoc.models.arcevents.ArcEvent;

import java.util.List;

public interface MetricsRepository {

  public void createEvent(ArcEvent event);

  public List<String> getEvents();
}
