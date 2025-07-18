# kafka-poc
Proof of concept for using Kafka with Protobuf and Glue Schema Registry

## Prerequisites
1. Add the following profile to ~/.aws/config, if it doesn't already exist
    ```bash
    [profile local]
    aws_access_key_id = test
    aws_secret_access_key = test
    aws_session_token = test
    endpoint_url = http://localhost:3000
    region = us-west-2
    output = json
    ```

## Running locally in Docker Compose
1. cd local
2. docker compose build
3. Open another terminal and trigger the producer to publish two messages to the "arc-events" Kafka topic:
    ```bash
    curl -X POST localhost:8090/publish -H 'Content-Type: application/json' -d '{"val": "hello"}'
    ```
4. There should now be one schema named "arc-events" in the "arc-registry" registry. You can verify by running:
    ```bash
    export AWS_PROFILE=local
    aws glue get-schema --schema-id '{"SchemaName": "arc-events", "RegistryName": "arc-registry"}'
    ```
5. You should also see some output in the first terminal indicating that the consumer has processed the two messages.
