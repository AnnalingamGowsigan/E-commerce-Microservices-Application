# Docker multi-stage build

#1. Building the App with Maven
FROM maven:3.9.4-eclipse-temurin-17-alpine

ADD . /app
WORKDIR /app

# Run Maven build
RUN mvn clean install

# 2. Just using the build artifact and then removing the build-container
FROM openjdk:17-alpine3.14

# Create a new user with UID 10014
RUN addgroup -g 10014 choreo && \
    adduser  --disabled-password  --no-create-home --uid 10014 --ingroup choreo choreouser

VOLUME /tmp

USER 10014

# Add Spring Boot app.jar to Container
COPY --from=0 "/app/target/*.jar" app.jar

# Expose the port your application will run on
EXPOSE 8080

# Fire up our Spring Boot app by default
ENTRYPOINT ["java","-jar","/app.jar"]