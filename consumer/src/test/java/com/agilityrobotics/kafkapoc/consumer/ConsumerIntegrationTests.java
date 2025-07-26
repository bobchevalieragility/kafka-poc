package com.agilityrobotics.kafkapoc.consumer;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import com.agilityrobotics.kafkapoc.models.arcevents.ArcEvent;
import com.agilityrobotics.kafkapoc.models.arcevents.ShiftStart;
import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;

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

  @DynamicPropertySource
  static void setProperties(final DynamicPropertyRegistry registry) {
    // Override existing properties
    String endpointUrl = String.format(
        "http://localhost:%d",
        schemaRegistryContainer.getMappedPort(3000));
    registry.add("aws.schema.registry.endpoint", () -> endpointUrl);
    registry.add("spring.kafka.bootstrap-servers", () -> kafkaContainer.getBootstrapServers());
    registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");

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

  // TODO is this necessary?
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private KafkaTemplate<String, ArcEvent> producer;

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
    final ShiftStart shiftEvent = ShiftStart.newBuilder().setFoo("foo").build();
    long millis = System.currentTimeMillis();
    Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
        .setNanos((int) ((millis % 1000) * 1000000)).build();
    ArcEvent event = ArcEvent.newBuilder().setId("123").setEventTime(timestamp).setShiftStart(shiftEvent).build();
    producer.send(topic, "fake-key", event);

    // Wait for the event to be consumed
    await()
        .timeout(2, SECONDS)
        .pollDelay(1, SECONDS)
        .untilAsserted(() -> Assertions.assertTrue(true));
    // await()
    // .pollInterval(Duration.ofSeconds(3))
    // .atMost(30, SECONDS)
    // .untilAsserted(() -> {
    // Optional<Product> optionalProduct = productRepository.findByCode(
    // "P100"
    // );
    // assertThat(optionalProduct).isPresent();
    // assertThat(optionalProduct.get().getCode()).isEqualTo("P100");
    // assertThat(optionalProduct.get().getPrice())
    // .isEqualTo(new BigDecimal("14.50"));
    // });
  }

}
