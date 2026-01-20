#!/bin/bash
set -e

# Get the build context directory (first argument)
BUILD_CONTEXT=${1:-.}
shift || true  # Remove first argument, keep rest for docker build args

# Get environment variables
USERNAME=${USERNAME:-}
REPO=${REPO:-}
TAG=${TAG:-latest}

if [ -z "$USERNAME" ] || [ -z "$REPO" ]; then
  echo "Error: USERNAME and REPO environment variables must be set"
  exit 1
fi

IMAGE_NAME="${USERNAME}/${REPO}"
FULL_IMAGE_NAME="${IMAGE_NAME}:${TAG}"

echo "Building Docker image: ${FULL_IMAGE_NAME}"
echo "Build context: ${BUILD_CONTEXT}"

# Build the Docker image with any additional build args
docker build -t "${FULL_IMAGE_NAME}" "$@" "${BUILD_CONTEXT}"

# Tag as latest
docker tag "${FULL_IMAGE_NAME}" "${IMAGE_NAME}:latest"

echo "Pushing ${FULL_IMAGE_NAME} to Docker Hub..."
docker push "${FULL_IMAGE_NAME}"

echo "Pushing ${IMAGE_NAME}:latest to Docker Hub..."
docker push "${IMAGE_NAME}:latest"

echo "Successfully pushed ${FULL_IMAGE_NAME} and ${IMAGE_NAME}:latest"
