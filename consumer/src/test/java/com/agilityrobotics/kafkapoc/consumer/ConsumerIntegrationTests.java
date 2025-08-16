package com.agilityrobotics.kafkapoc.consumer;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import com.agilityrobotics.kafkapoc.common.kafka.CloudEventKafkaTemplate;
import com.agilityrobotics.kafkapoc.consumer.repository.MetricsRepository;
import com.agilityrobotics.models.events.Organization;
import com.agilityrobotics.models.events.Shift;
import com.agilityrobotics.models.events.ShiftStarted;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.InfluxDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
@Testcontainers
class ConsumerIntegrationTests {

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

  @SuppressWarnings("resource")
  @Container
  static InfluxDBContainer<?> influxDbContainer = new InfluxDBContainer<>(
      DockerImageName.parse("influxdb:2"))
      .withOrganization("agility")
      .withBucket("metrics")
      .withAdminToken("admin-token");

  @DynamicPropertySource
  static void setProperties(final DynamicPropertyRegistry registry) {
    String schemaRegistryEndpoint = String.format(
        "http://localhost:%d",
        schemaRegistryContainer.getMappedPort(3000));
    String influxDbEndpoint = String.format("http://localhost:%d", influxDbContainer.getMappedPort(8086));

    // Override existing properties
    registry.add("aws.schema.registry.endpoint", () -> schemaRegistryEndpoint);
    registry.add("spring.kafka.bootstrap-servers", () -> kafkaContainer.getBootstrapServers());
    registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
    registry.add("arc.influxdb.url", () -> influxDbEndpoint);

    // Add producer properties for dummy producer
    registry.add("spring.kafka.producer.key-serializer",
        () -> "org.apache.kafka.common.serialization.StringSerializer");
    registry.add("spring.kafka.producer.value-serializer",
        () -> "com.amazonaws.services.schemaregistry.serializers.GlueSchemaRegistryKafkaSerializer");
    registry.add("aws.schema.registry.producer.compatibility", () -> "full");
    registry.add("aws.schema.registry.producer.schema-auto-registration-enabled", () -> "true");
  }

  @Value("${arc.kafka.arcevents.topic}")
  private String topic;

  @Autowired
  private CloudEventKafkaTemplate producer;

  @Autowired
  private MetricsRepository repository;

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
  void simpleTest() {
    final Organization org = Organization.newBuilder().setId("org123").setFacilityId("fac123").build();
    final ShiftStarted shiftEvent = ShiftStarted.newBuilder()
        .setOrg(org)
        .setShift(Shift.newBuilder().setId("shift123").build())
        .build();
    producer.sendEvent("test-source", topic, org, shiftEvent);

    // Wait for the event to be consumed
    await()
        .pollInterval(Duration.ofSeconds(2))
        .atMost(10, SECONDS)
        .timeout(2, SECONDS)
        .pollDelay(1, SECONDS)
        .untilAsserted(() -> {
          List<String> actual = repository.getEvents();
          Assertions.assertEquals(1, actual.size());
        });

    // Uncomment to wait forever so you can connect to testcontainer for debugging
    // await()
    // .forever()
    // .pollDelay(10, SECONDS)
    // .untilAsserted(() -> Assertions.assertTrue(false));
  }

}
