# Use an official Maven image as a base image
FROM maven:3.8.4-openjdk-17 AS build

# Set the working directory in the container
WORKDIR /

# Copy the project files into the container
COPY pom.xml .
COPY src ./src

# Build the Maven project inside the container
RUN mvn clean package

# Use an official OpenJDK image as a base image
FROM openjdk:17-jdk-alpine AS run

# Set the working directory in the container
WORKDIR /

# Copy the JAR file from the build stage into the container
COPY --from=build /target/queue-service-1.0.0.jar .

# Expose the port your application listens on
EXPOSE 8080

# Specify the command to run your application
CMD ["java", "-jar", "queue-service-1.0.0.jar"]
