#!/bin/bash

set -e

echo "Running app..."
  exec java \
    -Djava.security.egd=file:/dev/./urandom \
    -jar app.jar \
    --spring.cloud.bootstrap.enabled="false"
    --Drds_password=${SPRING_DATASOURCE_PASSWORD}
