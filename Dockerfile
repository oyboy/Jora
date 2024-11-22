FROM maven:3.8.5-jdk-11-slim AS build
WORKDIR /jora
COPY src ./src
COPY pom.xml .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} jora.jar
ENTRYPOINT ["java","-jar","/jora.jar"]