package com.agilityrobotics.kafkapoc.consumer.repository;

import com.agilityrobotics.kafkapoc.consumer.repository.model.EventMeasurement;
import com.agilityrobotics.models.events.ArcEvent;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxQLQueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.InfluxQLQuery;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.InfluxQLQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class InfluxDbMetricsRepository implements MetricsRepository {

  private final WriteApiBlocking writer;
  private final InfluxQLQueryApi reader;

  @Value("${arc.influxdb.bucket}")
  private String bucket;

  @Autowired
  public InfluxDbMetricsRepository(InfluxDBClient client) {
    this.writer = client.getWriteApiBlocking();
    this.reader = client.getInfluxQLQueryApi();
  }

  @Override
  public void createEvent(ArcEvent event) {
    Instant instant = Instant.ofEpochSecond(event.getEventTime().getSeconds(), event.getEventTime().getNanos());
    EventMeasurement measurement = new EventMeasurement(instant, event.getClass().getSimpleName(), "ON");
    this.writer.writeMeasurement(WritePrecision.NS, measurement);
  }

  @Override
  public List<String> getEvents() {
    String query = "SELECT * FROM \"event\"";
    InfluxQLQueryResult result = reader.queryCSV(new InfluxQLQuery(query, bucket));
    List<String> points = new ArrayList();
    for (InfluxQLQueryResult.Result queryResult : result.getResults()) {
      for (InfluxQLQueryResult.Series series : queryResult.getSeries()) {
        for (InfluxQLQueryResult.Series.Record record : series.getValues()) {
          points.add(record.getValueByKey("eventType").toString());
        }
      }
    }
    return points;
  }

}
