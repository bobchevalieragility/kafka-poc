package com.agilityrobotics.kafkapoc.producer;

import com.agilityrobotics.kafkapoc.common.kafka.ArcEventProducer;
import com.agilityrobotics.kafkapoc.models.arcevents.ArcEvent;
import com.agilityrobotics.kafkapoc.models.arcevents.ShiftStart;
import com.google.protobuf.Timestamp;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.util.List;

@SpringBootTest
@DirtiesContext
@Testcontainers
class ProducerIntegrationTests {

  @Container
  static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.9.1"));

  @SuppressWarnings("resource")
  @Container
  static GenericContainer<?> schemaRegistryContainer = new GenericContainer<>(
      DockerImageName.parse("motoserver/moto"))
      .withAccessToHost(true)
      .withExposedPorts(3000)
      .withEnv("MOTO_PORT", "3000")
      .withEnv("AWS_DEFAULT_REGION", "us-west-2")
      .withEnv("AWS_ACCESS_KEY_ID", "test")
      .withEnv("AWS_SECRET_ACCESS_KEY", "test")
      .withEnv("AWS_ENDPOINT_URL", "http://localhost:3000")
      .withCopyFileToContainer(
          MountableFile.forClasspathResource("moto_server_init"),
          "/moto/moto_server_init");

  @DynamicPropertySource
  static void setProperties(final DynamicPropertyRegistry registry) {
    // Override existing properties
    String endpointUrl = String.format(
        "http://localhost:%d",
        schemaRegistryContainer.getMappedPort(3000));
    registry.add("aws.schema.registry.endpoint", () -> endpointUrl);
    registry.add("spring.kafka.bootstrap-servers", () -> kafkaContainer.getBootstrapServers());

    // Add consumer properties for mock consumer
    registry.add("spring.kafka.consumer.group-id", () -> "foo");
    registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
    registry.add("spring.kafka.consumer.key-deserializer",
        () -> "org.apache.kafka.common.serialization.StringDeserializer");
    registry.add("spring.kafka.consumer.value-deserializer",
        () -> "com.amazonaws.services.schemaregistry.deserializers.GlueSchemaRegistryKafkaDeserializer");
  }

  @Autowired
  private ArcEventProducer producer;

  @Autowired
  private ConsumerFactory<String, ArcEvent> consumerFactory;

  @BeforeAll
  static void beforeAll() {
    // Allow the Kafka client to interact with AWS Glue Schema Registry
    System.setProperty("aws.accessKeyId", "test");
    System.setProperty("aws.secretAccessKey", "test");
  }

  @BeforeEach
  void beforeEach() throws IOException, InterruptedException {
    // Create a registry named 'arc-registry' in the Glue Schema Registry container
    schemaRegistryContainer.execInContainer("chmod", "+x", "/moto/moto_server_init");
    schemaRegistryContainer.execInContainer("/moto/moto_server_init");
  }

  @Test
  void simpleTest() throws InterruptedException {
    final ShiftStart shiftEvent = ShiftStart.newBuilder().setFoo("foo").build();
    long millis = System.currentTimeMillis();
    Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
        .setNanos((int) ((millis % 1000) * 1000000)).build();
    ArcEvent event = ArcEvent.newBuilder().setId("123").setEventTime(timestamp).setShiftStart(shiftEvent).build();

    String topic = "arc-events";
    producer.publish(topic, event);

    Consumer<String, ArcEvent> consumer = consumerFactory.createConsumer();
    consumer.subscribe(List.of(topic));
    ConsumerRecord<String, ArcEvent> rec = KafkaTestUtils.getSingleRecord(consumer, topic);
    System.out.println(rec.value().toString());
  }

}
