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
3. Open another terminal and trigger the producer to publish a message to Kafka:
    ```bash
    curl -X POST localhost:8090/publish -d 'hello'
    ```
4. There should now be a schema in a registry named "arc-registry". You can verify by running:
    ```bash
    export AWS_PROFILE=local
    aws glue get-schema --schema-id '{"SchemaName": "arcevents.workcell.ShiftStart", "RegistryName": "arc-registry"}'
    ```
