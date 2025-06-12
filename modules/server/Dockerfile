# Use an official lightweight OpenJDK base image
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

ENV KUBERNETES_URL="kubernetes.default.svc"

EXPOSE 9393
EXPOSE 50052

# Copy the JAR file into the image
COPY build/libs/server-1.0.0-all.jar app.jar

# Set the entry point to run the jar
ENTRYPOINT ["java", "-Xmx500m", "-jar", "app.jar"]
