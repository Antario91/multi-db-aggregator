FROM gradle:8.5.0-jdk21 AS build
WORKDIR /workspace/app

COPY src/main ./src/main
COPY build.gradle ./

#COPY support/src/main ./support/src/main
#COPY support/build.gradle ./support

COPY build.gradle settings.gradle  ./

RUN gradle clean build

FROM eclipse-temurin:21.0.3_9-jdk-alpine AS run

COPY --from=build /workspace/app/build/libs/multi-db-aggregator-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]