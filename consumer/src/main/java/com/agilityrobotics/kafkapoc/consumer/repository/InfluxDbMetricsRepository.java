package com.agilityrobotics.kafkapoc.consumer.repository;

import com.agilityrobotics.kafkapoc.consumer.repository.model.EventMeasurement;
import com.agilityrobotics.kafkapoc.models.arcevents.ArcEvent;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public class InfluxDbMetricsRepository implements MetricsRepository {

  // private final InfluxDBClient client;

  private final WriteApiBlocking writer;

  @Autowired
  public InfluxDbMetricsRepository(InfluxDBClient client) {
    // this.client = client;
    this.writer = client.getWriteApiBlocking();
  }

  @Override
  public void createEvent(ArcEvent event) {
    Instant instant = Instant.ofEpochSecond(event.getEventTime().getSeconds(), event.getEventTime().getNanos());
    EventMeasurement measurement = new EventMeasurement(instant, event.getClass().getSimpleName(), "ON");
    this.writer.writeMeasurement(WritePrecision.NS, measurement);
  }

}
