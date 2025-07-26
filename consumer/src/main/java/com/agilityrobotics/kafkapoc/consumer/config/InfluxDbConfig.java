package com.agilityrobotics.kafkapoc.consumer.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDbConfig {

  @Value("${arc.influxdb.url}")
  private String url;

  @Value("${arc.influxdb.token}")
  private String token;

  @Value("${arc.influxdb.org}")
  private String org;

  @Value("${arc.influxdb.bucket}")
  private String bucket;

  @Bean
  public InfluxDBClient influxDbClient() {
    final InfluxDBClientOptions options = InfluxDBClientOptions
        .builder()
        .url(url)
        .authenticateToken(token.toCharArray())
        .org(org)
        .bucket(bucket)
        .build();
    return InfluxDBClientFactory.create(options);
  }

}
