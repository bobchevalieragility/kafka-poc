package com.agilityrobotics.producer;

import com.agilityrobotics.models.arcevents.ArcEvent;
import com.agilityrobotics.models.arcevents.ShiftStart;
import com.google.protobuf.Timestamp;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.utils.ContainerTestUtils;
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
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@DirtiesContext
@Testcontainers
class ProducerApplicationTests {

  @Container
  static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.9.1"));

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

    // Add consumer properties for test consumer
    registry.add("spring.kafka.consumer.group-id", () -> "foo");
    registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
    registry.add("spring.kafka.consumer.key-deserializer",
        () -> "org.apache.kafka.common.serialization.StringDeserializer");
    registry.add("spring.kafka.consumer.value-deserializer",
        () -> "com.amazonaws.services.schemaregistry.deserializers.GlueSchemaRegistryKafkaDeserializer");
  }

  @Autowired
  private EventPublisher publisher;

  @Autowired
  private ConsumerFactory<String, ArcEvent> consumerFactory;

  private KafkaMessageListenerContainer<String, ArcEvent> listener;
  // private BlockingQueue<ArcEvent> messages;
  private BlockingQueue<ConsumerRecord<String, ArcEvent>> messages;

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

    messages = new LinkedBlockingQueue<>();

    Map<String, Object> consumerProps = consumerFactory.getConfigurationProperties();
    ContainerProperties containerProps = new ContainerProperties("arc-events");
    containerProps.setGroupId((String) consumerProps.get(ConsumerConfig.GROUP_ID_CONFIG));
    containerProps.setPollTimeout(100);
    listener = new KafkaMessageListenerContainer<>(consumerFactory, containerProps);
    listener.setupMessageListener((MessageListener<String, ArcEvent>) messages::add);
    listener.start();
    ContainerTestUtils.waitForAssignment(listener, 1);
  }

  @Test
  void simpleTest() throws InterruptedException {
    final ShiftStart shiftEvent = ShiftStart.newBuilder().setFoo("foo").build();
    long millis = System.currentTimeMillis();
    Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
        .setNanos((int) ((millis % 1000) * 1000000)).build();
    ArcEvent msg = ArcEvent.newBuilder().setId("123").setEventTime(timestamp).setShiftStart(shiftEvent).build();
    publisher.sendMessage("arc-events", msg);

    ArcEvent event = messages.poll(5, TimeUnit.SECONDS).value();
    System.out.println(event.toString());
    listener.stop();
  }

}
