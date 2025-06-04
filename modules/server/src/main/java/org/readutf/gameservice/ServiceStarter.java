package org.readutf.gameservice;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import java.io.IOException;
import org.readutf.gameservice.api.GameServiceEndpoint;
import org.readutf.gameservice.container.docker.DockerContainerPlatform;
import org.readutf.gameservice.server.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceStarter {

    private static final Logger log = LoggerFactory.getLogger(ServiceStarter.class);

    public static void main(String[] args) throws InterruptedException, IOException {

        log.info("Starting server...");

        var platform = new DockerContainerPlatform();

        var serverManager = new ServerManager(platform);


        Server server = Grpc.newServerBuilderForPort(50052, InsecureServerCredentials.create())
                .addService(new GameServiceEndpoint(serverManager))
                .build();

        server.start();

        log.info("GRPC server started on [::0]:50052");

        server.awaitTermination();
    }
}
