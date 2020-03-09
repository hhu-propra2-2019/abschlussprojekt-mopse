#!/usr/bin/env sh

docker-compose -f docker-compose.yml -f docker-compose.dev.yml pull
docker pull gradle:jdk11
docker pull openjdk:11-jre-slim
docker-compose -f docker-compose.yml -f docker-compose.dev.yml build
