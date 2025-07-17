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
3. Open another terminal
4. `export AWS_PROFILE=local`
5. curl -X POST localhost:8090/publish -d 'hello'
6. There should now be a single version of the ??? schema in the arc-registry. You can verify by running: `aws glue get-schema --schema-id '{"SchemaName": "???", "RegistryName": "arc-registry"}'
7. The consumer logs should show that a message was received on the arc-events topic
