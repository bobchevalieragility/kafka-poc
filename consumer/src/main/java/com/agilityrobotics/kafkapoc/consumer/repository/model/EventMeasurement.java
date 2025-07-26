package com.agilityrobotics.kafkapoc.consumer.repository.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import java.time.Instant;

@Measurement(name = "event")
public record EventMeasurement(
    @Column(timestamp = true) Instant time,

    @Column(tag = true) String eventType,

    @Column String shiftState) {
};
