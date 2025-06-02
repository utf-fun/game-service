package org.readutf.gameservice;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import java.io.IOException;
import org.readutf.gameservice.api.GameServiceEndpoint;
import org.readutf.gameservice.server.ServerManager;

public class ServiceStarter {

    public static void main(String[] args) throws InterruptedException, IOException {

        var serverManager = new ServerManager();

        Server server = Grpc.newServerBuilderForPort(50052, InsecureServerCredentials.create())
                .addService(new GameServiceEndpoint(serverManager))
                .build();

        server.start();
        server.awaitTermination();
    }
}
