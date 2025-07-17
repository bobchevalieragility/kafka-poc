package com.agilityrobotics.models.protobuf;

import com.amazonaws.services.schemaregistry.common.AWSSchemaNamingStrategy;
import com.google.protobuf.Message;

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
