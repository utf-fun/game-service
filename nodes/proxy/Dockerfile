FROM eclipse-temurin:23-jdk-alpine

# Set the working directory
WORKDIR /app

EXPOSE 25565

ADD https://fill-data.papermc.io/v1/objects/f82780ce33035ebe3d6ea7981f0e6e8a3e41a64f2080ef5c0f1266fada03cbee/velocity-3.4.0-SNAPSHOT-522.jar velocity.jar

COPY build/libs/discovery-velocity.jar ./plugins/


# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "velocity.jar"]
