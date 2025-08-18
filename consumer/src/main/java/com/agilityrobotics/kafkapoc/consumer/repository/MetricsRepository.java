package com.agilityrobotics.kafkapoc.consumer.repository;

import io.cloudevents.CloudEvent;

import java.util.List;

public interface MetricsRepository {

  public void createEvent(CloudEvent event);

  public List<String> getEvents();
}
