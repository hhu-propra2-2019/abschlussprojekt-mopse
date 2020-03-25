FROM gradle:jdk11 AS BUILD
WORKDIR /material1/build
COPY . .
RUN ./gradlew bootJar

FROM openjdk:11-jre-slim
WORKDIR /material1/run
COPY --from=BUILD /material1/build/build/libs/*.jar app.jar
COPY wait-for-it.sh wait-for-it.sh
RUN ["chmod", "+x", "wait-for-it.sh"]
EXPOSE 8080
CMD ["./wait-for-it.sh", "material1_db:5432", "--timeout=0", "--", "java", "-jar", "-Dspring.profiles.active=${MATERIAL1_PROFILE}", "app.jar"]
