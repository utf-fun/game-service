FROM eclipse-temurin:23-jdk-alpine

# Set the working directory
WORKDIR /app

EXPOSE 25565

# Copy the application distribution
COPY build/install/tnttag ./

# Make the startup script executable
RUN chmod +x ./bin/tnttag

# Set the entry point to run the application
ENTRYPOINT ["./bin/tnttag"]