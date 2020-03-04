#!/usr/bin/env sh

docker-compose pull
docker pull gradle:jdk11
docker pull openjdk:11-jre-slim
docker-compose build
