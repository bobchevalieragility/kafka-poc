package com.agilityrobotics.kafkapoc.producer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.cloudevents.CloudEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
@Testcontainers
class ProducerIntegrationTests {

  @Container
  static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.9.1"));

  // @SuppressWarnings("resource")
  // @Container
  // static GenericContainer<?> schemaRegistryContainer = new GenericContainer<>(
  // DockerImageName.parse("motoserver/moto"))
  // .withAccessToHost(true)
  // .withExposedPorts(3000)
  // .withEnv("MOTO_PORT", "3000")
  // .withEnv("AWS_DEFAULT_REGION", "us-west-2")
  // .withEnv("AWS_ACCESS_KEY_ID", "test")
  // .withEnv("AWS_SECRET_ACCESS_KEY", "test")
  // .withEnv("AWS_ENDPOINT_URL", "http://localhost:3000")
  // .withCopyFileToContainer(
  // MountableFile.forClasspathResource("moto_server_init"),
  // "/moto/moto_server_init");

  @DynamicPropertySource
  static void setProperties(final DynamicPropertyRegistry registry) {
    // Override existing properties
    // String endpointUrl = String.format(
    // "http://localhost:%d",
    // schemaRegistryContainer.getMappedPort(3000));
    // registry.add("aws.schema.registry.endpoint", () -> endpointUrl);
    registry.add("spring.kafka.bootstrap-servers", () -> kafkaContainer.getBootstrapServers());

    // Add consumer properties for dummy consumer
    registry.add("spring.kafka.consumer.group-id", () -> "foo");
    registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
    registry.add("spring.kafka.consumer.key-deserializer",
        () -> "org.apache.kafka.common.serialization.StringDeserializer");
    // registry.add("aws.schema.registry.consumer.protobuf-message-type", () ->
    // "pojo");
    // registry.add("spring.kafka.consumer.value-deserializer",
    // () ->
    // "com.amazonaws.services.schemaregistry.deserializers.GlueSchemaRegistryKafkaDeserializer");
    registry.add("spring.kafka.consumer.value-deserializer",
        () -> "io.cloudevents.kafka.CloudEventDeserializer");
  }

  @Value("${arc.kafka.arcevents.topic}")
  private String topic;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ConsumerFactory<String, CloudEvent> consumerFactory;

  @BeforeAll
  static void beforeAll() {
    // Allow the Kafka client to interact with AWS Glue Schema Registry
    System.setProperty("aws.accessKeyId", "test");
    System.setProperty("aws.secretAccessKey", "test");
  }

  // @BeforeEach
  // void beforeEach() throws IOException, InterruptedException {
  // // Create a registry named 'arc-registry' in the Glue Schema Registry
  // container
  // schemaRegistryContainer.execInContainer("chmod", "+x",
  // "/moto/moto_server_init");
  // schemaRegistryContainer.execInContainer("/moto/moto_server_init");
  // }

  @Test
  void publishProducesTwoEvents() throws Exception {
    mockMvc.perform(post("/publish")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"val\": \"hello\"}"))
        .andExpect(status().isOk());

    Consumer<String, CloudEvent> consumer = consumerFactory.createConsumer();
    consumer.subscribe(List.of(topic));
    ConsumerRecords<String, CloudEvent> recs = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(30), 2);
    Assertions.assertEquals(2, recs.count(), "Expected 2 records to be published.");
  }

}
