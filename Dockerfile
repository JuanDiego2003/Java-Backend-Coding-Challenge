FROM openjdk:21-jdk-slim AS builder
RUN apt-get update && apt-get install -y maven

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

CMD ["mvn", "spring-boot:run"]