#!/bin/sh

TIMEOUT=10
INTERVAL=3
URL="http://moto-server:3000/moto-api"

# Loop until we're able to reach the moto server, or we timeout
max_attempts=$((TIMEOUT / INTERVAL))
count=0
until curl --fail --silent --output /dev/null $URL; do
  count=$((count + 1))

  # Check to see if we've reached our maximum number of attempts
  if [ $count -eq $max_attempts ]; then
    echo "Timed out waiting for moto server at $URL"
    exit 1
  fi

  echo "Waiting for moto server..."
  sleep "$INTERVAL"
done

## Create WorkflowStatus DynamoDB table
#/usr/local/bin/aws dynamodb create-table \
#    --table-name WorkflowStatus \
#    --key-schema \
#        "AttributeName=workflowId,KeyType=HASH" \
#        "AttributeName=robotId,KeyType=RANGE" \
#    --attribute-definitions \
#        "AttributeName=workflowId,AttributeType=S" \
#        "AttributeName=robotId,AttributeType=S" \
#        "AttributeName=lastRunStatus,AttributeType=S" \
#        "AttributeName=updateTime,AttributeType=S" \
#    --global-secondary-indexes '[
#        {
#            "IndexName": "lastRunStatusIndex",
#            "KeySchema": [
#                {"AttributeName": "lastRunStatus", "KeyType": "HASH"},
#                {"AttributeName": "updateTime", "KeyType": "RANGE"}
#            ],
#            "Projection": {
#                "ProjectionType": "ALL"
#            }
#        },
#        {
#            "IndexName": "workflowIdIndex",
#            "KeySchema": [
#                {"AttributeName": "workflowId", "KeyType": "HASH"}
#            ],
#            "Projection": {
#                "ProjectionType": "ALL"
#            }
#        }
#    ]' \
#    --billing-mode PAY_PER_REQUEST > /dev/null
#if [ $? -eq 0 ]; then
#  echo "Created WorkflowStatus DynamoDB table"
#fi
#
## Create WorkflowExecution DynamoDB table
#/usr/local/bin/aws dynamodb create-table \
#    --table-name WorkflowExecution \
#    --key-schema \
#        "AttributeName=workflowExecutionId,KeyType=HASH" \
#    --attribute-definitions \
#        "AttributeName=workflowExecutionId,AttributeType=S" \
#        "AttributeName=temporalExecutionId,AttributeType=S" \
#    --global-secondary-indexes '[
#        {
#            "IndexName": "temporalExecutionIdIndex",
#            "KeySchema": [
#                {"AttributeName": "temporalExecutionId", "KeyType": "HASH"}
#            ],
#            "Projection": {
#                "ProjectionType": "ALL"
#            }
#        }
#    ]' \
#    --billing-mode PAY_PER_REQUEST > /dev/null
#if [ $? -eq 0 ]; then
#  echo "Created WorkflowExecution DynamoDB table"
#fi
#
## Create WorkflowMetrics DynamoDB table
#/usr/local/bin/aws dynamodb create-table \
#    --table-name WorkflowMetrics \
#    --key-schema \
#        "AttributeName=workflowRunId,KeyType=HASH" \
#    --attribute-definitions \
#        "AttributeName=workflowRunId,AttributeType=S" \
#        "AttributeName=workcellId,AttributeType=S" \
#        "AttributeName=startTime,AttributeType=S" \
#    --global-secondary-indexes '[
#        {
#            "IndexName": "workcellIdIndex",
#            "KeySchema": [
#                {"AttributeName": "workcellId", "KeyType": "HASH"},
#                {"AttributeName": "startTime", "KeyType": "RANGE"}
#            ],
#            "Projection": {
#                "ProjectionType": "ALL"
#            }
#        }
#    ]' \
#    --billing-mode PAY_PER_REQUEST > /dev/null
#if [ $? -eq 0 ]; then
#  echo "Created WorkflowMetrics DynamoDB table"
#fi
#
## Create WorkflowMetadata DynamoDB table
#/usr/local/bin/aws dynamodb create-table \
#    --table-name WorkflowMetadata \
#    --key-schema \
#        "AttributeName=workflowId,KeyType=HASH" \
#    --attribute-definitions \
#        "AttributeName=workflowId,AttributeType=S" \
#        "AttributeName=workcellId,AttributeType=S" \
#    --global-secondary-indexes '[
#        {
#            "IndexName": "workcellId-index",
#            "KeySchema": [
#                {"AttributeName": "workcellId", "KeyType": "HASH"}
#            ],
#            "Projection": {
#                "ProjectionType": "ALL"
#            }
#        }
#    ]' \
#    --billing-mode PAY_PER_REQUEST > /dev/null
#if [ $? -eq 0 ]; then
#  echo "Created WorkflowMetadata DynamoDB table"
#fi
#
## Create MessageStatus DynamoDB table
#/usr/local/bin/aws dynamodb create-table \
#    --table-name MessageStatus \
#    --key-schema \
#        "AttributeName=messageId,KeyType=HASH" \
#    --attribute-definitions \
#        "AttributeName=messageId,AttributeType=S" \
#    --billing-mode PAY_PER_REQUEST > /dev/null
#if [ $? -eq 0 ]; then
#  echo "Created MessageStatus DynamoDB table"
#fi
#
## Create FleetStatus DynamoDB table
#/usr/local/bin/aws dynamodb create-table \
#    --table-name FleetStatus \
#    --key-schema \
#        "AttributeName=robotId,KeyType=HASH" \
#    --attribute-definitions \
#        "AttributeName=robotId,AttributeType=S" \
#        "AttributeName=workcellId,AttributeType=S" \
#    --global-secondary-indexes '[
#        {
#            "IndexName": "workcellIdIndex",
#            "KeySchema": [
#                {"AttributeName": "workcellId", "KeyType": "HASH"}
#            ],
#            "Projection": {
#                "ProjectionType": "ALL"
#            }
#        }
#    ]' \
#    --billing-mode PAY_PER_REQUEST > /dev/null
#if [ $? -eq 0 ]; then
#  echo "Created FleetStatus DynamoDB table"
#fi
#
## Create FleetIncidents DynamoDB table
#/usr/local/bin/aws dynamodb create-table \
#    --table-name FleetIncidents \
#    --key-schema \
#        "AttributeName=incidentId,KeyType=HASH" \
#    --attribute-definitions \
#        "AttributeName=incidentId,AttributeType=S" \
#        "AttributeName=workcellId,AttributeType=S" \
#        "AttributeName=robotId,AttributeType=S" \
#    --global-secondary-indexes '[
#        {
#            "IndexName": "workcellIdIndex",
#            "KeySchema": [
#                {"AttributeName": "workcellId", "KeyType": "HASH"}
#            ],
#            "Projection": {
#                "ProjectionType": "ALL"
#            }
#        },
#        {
#            "IndexName": "robotIdIndex",
#            "KeySchema": [
#                {"AttributeName": "robotId", "KeyType": "HASH"}
#            ],
#            "Projection": {
#                "ProjectionType": "ALL"
#            }
#        }
#    ]' \
#    --billing-mode PAY_PER_REQUEST > /dev/null
#if [ $? -eq 0 ]; then
#  echo "Created FleetIncidents DynamoDB table"
#fi
#
## Create WebhookMetadata DynamoDB table
#/usr/local/bin/aws dynamodb create-table \
#    --table-name WebhookMetadata \
#    --key-schema \
#        "AttributeName=amrId,KeyType=HASH" \
#    --attribute-definitions \
#        "AttributeName=amrId,AttributeType=S" \
#        "AttributeName=receivedTime,AttributeType=S" \
#        "AttributeName=tempId,AttributeType=S" \
#    --global-secondary-indexes '[
#        {
#            "IndexName": "receivedTime-index",
#            "KeySchema": [
#                {"AttributeName": "tempId", "KeyType": "HASH"},
#                {"AttributeName": "receivedTime", "KeyType": "RANGE"}
#            ],
#            "Projection": {
#                "ProjectionType": "ALL"
#            }
#        }
#    ]' \
#    --billing-mode PAY_PER_REQUEST > /dev/null
#if [ $? -eq 0 ]; then
#  echo "Created WebhookMetadata DynamoDB table"
#fi
#
## Create GenericWebhookMetadata DynamoDB table
#/usr/local/bin/aws dynamodb create-table \
#    --table-name GenericWebhookMetadata \
#    --key-schema \
#        "AttributeName=webhookId,KeyType=HASH" \
#    --attribute-definitions \
#        "AttributeName=webhookId,AttributeType=S" \
#    --billing-mode PAY_PER_REQUEST > /dev/null
#if [ $? -eq 0 ]; then
#  echo "Created GenericWebhookMetadata DynamoDB table"
#fi
#
## Create EventResourceMetadata DynamoDB table
#/usr/local/bin/aws dynamodb create-table \
#    --table-name EventResourceMetadata \
#    --key-schema \
#        "AttributeName=amrId,KeyType=HASH" \
#    --attribute-definitions \
#        "AttributeName=amrId,AttributeType=S" \
#        "AttributeName=receivedTime,AttributeType=S" \
#        "AttributeName=locationId,AttributeType=S" \
#    --global-secondary-indexes '[
#        {
#            "IndexName": "receivedTime-index",
#            "KeySchema": [
#                {"AttributeName": "locationId", "KeyType": "HASH"},
#                {"AttributeName": "receivedTime", "KeyType": "RANGE"}
#            ],
#            "Projection": {
#                "ProjectionType": "ALL"
#            }
#        }
#    ]' \
#    --billing-mode PAY_PER_REQUEST > /dev/null
#if [ $? -eq 0 ]; then
#  echo "Created EventResourceMetadata DynamoDB table"
#fi
#
## Create WorkflowSchedule DynamoDB table
#/usr/local/bin/aws dynamodb create-table \
#    --table-name WorkflowSchedule \
#    --key-schema \
#        "AttributeName=scheduleId,KeyType=HASH" \
#    --attribute-definitions \
#        "AttributeName=scheduleId,AttributeType=S" \
#        "AttributeName=workflowId,AttributeType=S" \
#        "AttributeName=robotPoolId,AttributeType=S" \
#        "AttributeName=chargerPoolId,AttributeType=S" \
#        "AttributeName=orgId,AttributeType=S" \
#    --global-secondary-indexes '[
#        {
#            "IndexName": "workflowIdIndex",
#            "KeySchema": [
#                {"AttributeName": "workflowId", "KeyType": "HASH"}
#            ],
#            "Projection": {
#                "ProjectionType": "ALL"
#            }
#        },
#        {
#            "IndexName": "robotPoolIdIndex",
#            "KeySchema": [
#                {"AttributeName": "robotPoolId", "KeyType": "HASH"}
#            ],
#            "Projection": {
#                "ProjectionType": "ALL"
#            }
#        },
#        {
#            "IndexName": "chargerPoolIdIndex",
#            "KeySchema": [
#                {"AttributeName": "chargerPoolId", "KeyType": "HASH"}
#            ],
#            "Projection": {
#                "ProjectionType": "ALL"
#            }
#        },
#        {
#            "IndexName": "orgIdIndex",
#            "KeySchema": [
#                {"AttributeName": "orgId", "KeyType": "HASH"}
#            ],
#            "Projection": {
#                "ProjectionType": "ALL"
#            }
#        }
#    ]' \
#    --billing-mode PAY_PER_REQUEST > /dev/null
#if [ $? -eq 0 ]; then
#  echo "Created WorkflowSchedule DynamoDB table"
#fi
#
## Create SQS queues
#/usr/local/bin/aws sqs create-queue --queue-name event_resource_management
#/usr/local/bin/aws sqs create-queue --queue-name charger_reserve
#/usr/local/bin/aws sqs create-queue --queue-name device_pool_event
#
## Create S3 buckets
#/usr/local/bin/aws s3 mb s3://agility-workflow-code-files-local
#
## Start the main application
##exec "$@"
#
#/usr/local/bin/aws kafka create-cluster --cluster-name platform-local-uw2 \
#  --broker-node-group-info file://brokernodegroupinfo.json \
#  --kafka-version "2.8.0" \
#  --number-of-broker-nodes 3
#  --kafka-version "4.0.0" \
#  --number-of-broker-nodes 1

#tail -f /dev/null
