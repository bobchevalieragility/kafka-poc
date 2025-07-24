package com.agilityrobotics.kafkapoc.models;

import com.amazonaws.services.schemaregistry.common.AWSSchemaNamingStrategy;
import com.google.protobuf.Message;

/**
 * We only need this class if we want to store schemas by "record name"
 * instead of "topic name" in the AWS Glue Schema Registry.
 * To make use of this class, set the following in the KafkaProducerConfig and
 * KafkaConsumerConfig:
 *
 * props.put(
 * AWSSchemaRegistryConstants.SCHEMA_NAMING_GENERATION_CLASS,
 * com.agilityrobotics.models.protobuf.RecordSchemaNamingStrategy"
 * );
 */
public class RecordSchemaNamingStrategy implements AWSSchemaNamingStrategy {

  @Override
  public String getSchemaName(String transportName, Object data) {
    return ((Message) data).getDescriptorForType().getFullName();
  }

  @Override
  public String getSchemaName(String transportName) {
    return transportName;
  }

}
