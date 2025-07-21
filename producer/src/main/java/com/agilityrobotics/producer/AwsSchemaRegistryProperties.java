package com.agilityrobotics.producer;

import com.amazonaws.services.schemaregistry.utils.AWSSchemaRegistryConstants;
import com.amazonaws.services.schemaregistry.utils.ProtobufMessageType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.glue.model.Compatibility;
import software.amazon.awssdk.services.glue.model.DataFormat;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for Spring for Apache Kafka.
 * <p>
 * Users should refer to Kafka documentation for complete descriptions of these
 * properties.
 */
@ConfigurationProperties(prefix = "aws.schema.registry")
@Component
public class AwsSchemaRegistryProperties {
  private DataFormat dataFormat;
  private String endpoint;
  private String region;
  private String registryName;
  private String schemaNamingClass;
  private Consumer consumer = new Consumer();
  private Producer producer = new Producer();

  public DataFormat getDataFormat() {
    return this.dataFormat;
  }

  public void setDataFormat(final DataFormat dataFormat) {
    this.dataFormat = dataFormat;
  }

  public String getEndpoint() {
    return this.endpoint;
  }

  public void setEndpoint(final String endpoint) {
    this.endpoint = endpoint;
  }

  public String getRegion() {
    return this.region;
  }

  public void setRegion(final String region) {
    this.region = region;
  }

  public String getRegistryName() {
    return this.registryName;
  }

  public void setRegistryName(final String region) {
    this.registryName = region;
  }

  public String getSchemaNamingClass() {
    return this.schemaNamingClass;
  }

  public void setSchemaNamingClass(final String schemaNamingClass) {
    this.schemaNamingClass = schemaNamingClass;
  }

  public Consumer getConsumer() {
    return this.consumer;
  }

  public Producer getProducer() {
    return this.producer;
  }

  private Map<String, Object> buildCommonProperties() {
    Map<String, Object> props = new HashMap<>();
    if (this.dataFormat != null) {
      props.put(AWSSchemaRegistryConstants.DATA_FORMAT, this.dataFormat.name());
    }
    if (this.endpoint != null) {
      props.put(AWSSchemaRegistryConstants.AWS_ENDPOINT, this.endpoint);
    }
    if (this.region != null) {
      props.put(AWSSchemaRegistryConstants.AWS_REGION, this.region);
    }
    if (this.registryName != null) {
      props.put(AWSSchemaRegistryConstants.REGISTRY_NAME, this.registryName);
    }
    if (this.schemaNamingClass != null) {
      props.put(AWSSchemaRegistryConstants.SCHEMA_NAMING_GENERATION_CLASS, this.schemaNamingClass);
    }
    return props;
  }

  public Map<String, Object> buildConsumerProperties() {
    Map<String, Object> props = buildCommonProperties();
    props.putAll(this.consumer.buildProperties());
    return props;
  }

  public Map<String, Object> buildProducerProperties() {
    Map<String, Object> props = buildCommonProperties();
    props.putAll(this.producer.buildProperties());
    return props;
  }

  public static class Consumer {
    private ProtobufMessageType protobufMessageType;

    public ProtobufMessageType getProtobufMessageType() {
      return this.protobufMessageType;
    }

    public void setProtobufMessageType(final ProtobufMessageType protobufMessageType) {
      this.protobufMessageType = protobufMessageType;
    }

    public Map<String, Object> buildProperties() {
      Map<String, Object> props = new HashMap<>();
      if (this.protobufMessageType != null) {
        props.put(AWSSchemaRegistryConstants.PROTOBUF_MESSAGE_TYPE, this.protobufMessageType);
      }
      return props;
    }
  }

  public static class Producer {
    private Boolean schemaAutoRegistrationEnabled;
    private Compatibility compatibility;

    public Boolean getSchemaAutoRegistrationEnabled() {
      return this.schemaAutoRegistrationEnabled;
    }

    public void setSchemaAutoRegistrationEnabled(final Boolean schemaAutoRegistrationEnabled) {
      this.schemaAutoRegistrationEnabled = schemaAutoRegistrationEnabled;
    }

    public Compatibility getCompatibility() {
      return this.compatibility;
    }

    public void setCompatibility(final Compatibility compatibility) {
      this.compatibility = compatibility;
    }

    public Map<String, Object> buildProperties() {
      Map<String, Object> props = new HashMap<>();
      if (this.schemaAutoRegistrationEnabled != null) {
        props.put(AWSSchemaRegistryConstants.SCHEMA_AUTO_REGISTRATION_SETTING,
            this.schemaAutoRegistrationEnabled.toString());
      }
      if (this.compatibility != null) {
        props.put(AWSSchemaRegistryConstants.COMPATIBILITY_SETTING, this.compatibility);
      }
      return props;
    }
  }
}
