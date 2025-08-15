package com.agilityrobotics.kafkapoc.consumer.repository;

import io.cloudevents.v1.proto.CloudEvent;

import java.util.List;

public interface MetricsRepository {

  public void createEvent(CloudEvent event);

  public List<String> getEvents();
}
