package com.agilityrobotics.kafkapoc.consumer.repository;

import com.agilityrobotics.models.events.ArcEvent;

import java.util.List;

public interface MetricsRepository {

  public void createEvent(ArcEvent event);

  public List<String> getEvents();
}
