#!/bin/bash

set -e

cd $(dirname $0)/

echo "building artifacts"
./gradlew clean build

echo "Building image"
docker build -t fabrick-account-service:local .

echo "Running container"
docker container run -p 8080:8080 -d fabrick-account-service:local
