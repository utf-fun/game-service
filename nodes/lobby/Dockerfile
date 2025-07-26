FROM eclipse-temurin:23-jdk-alpine

# Set the working directory
WORKDIR /app

EXPOSE 25565

# Copy the application distribution
COPY build/install/Lobby ./

# Make the startup script executable
RUN chmod +x ./bin/Lobby

# Set the entry point to run the application
ENTRYPOINT ["./bin/Lobby"]
