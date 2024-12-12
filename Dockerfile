# Use a lightweight JDK base image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
# Replace 'target/pos-system-1.0.0.jar' with the actual path to your built JAR file
COPY target/pos-system-1.0.0.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Set the command to run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]