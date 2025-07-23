# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jre

# Set the working directory
WORKDIR /app

# Copy the built jar file from target folder
COPY target/xcentralback-0.0.1-SNAPSHOT.jar app.jar

# Expose the port (Render will set $PORT)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]