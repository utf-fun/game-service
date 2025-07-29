FROM eclipse-temurin:23-jdk-alpine

# Set the working directory
WORKDIR /app

EXPOSE 25565

# Copy the application distribution
COPY build/install/lobby ./

# Make the startup script executable
RUN chmod +x ./bin/lobby

# Set the entry point to run the application
ENTRYPOINT ["./bin/lobby"]
