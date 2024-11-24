FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /jora

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -X -DskipTests

FROM openjdk:17-jdk-alpine
COPY --from=build /jora/target/*.jar jora.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","jora.jar"]