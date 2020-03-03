FROM gradle:jdk11 AS BUILD
WORKDIR /material1/build
COPY . .
RUN gradle bootJar

FROM openjdk:11-jre-slim
ARG MATERIAL1_PORT
WORKDIR /material1/run
COPY --from=BUILD /material1/build/build/libs/*.jar app.jar
EXPOSE ${MATERIAL1_PORT}
ENV MATERIAL1_PORT ${MATERIAL1_PORT}
CMD ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
