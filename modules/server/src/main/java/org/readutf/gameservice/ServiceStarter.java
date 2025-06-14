package org.readutf.gameservice;

import io.grpc.Server;

import java.io.IOException;
import java.net.InetSocketAddress;

import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.javalin.Javalin;
import io.kubernetes.client.openapi.ApiException;
import org.readutf.gameservice.api.RouteLogger;
import org.readutf.gameservice.api.routes.ListServersEndpoint;
import org.readutf.gameservice.container.kubernetes.KubernetesPlatform;
import org.readutf.gameservice.grpc.GameService;
import org.readutf.gameservice.container.docker.DockerContainerPlatform;
import org.readutf.gameservice.server.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceStarter {

    private static final Logger log = LoggerFactory.getLogger(ServiceStarter.class);

    public static void main(String[] args) throws InterruptedException, IOException, ApiException {

        log.info("Starting server...");

        var platform = new KubernetesPlatform(System.getenv("KUBERNETES_URL"), System.getenv("KUBERNETES_TOKEN"));
        var serverManager = new ServerManager(platform);

        Server server = NettyServerBuilder.forAddress(new InetSocketAddress("0.0.0.0", 50052)).addService(new GameService(serverManager)).build().start();

        Javalin.create(config -> config.showJavalinBanner = false)
                .after(new RouteLogger())
                .get("/api/v1/server", new ListServersEndpoint(serverManager))
                .start("0.0.0.0", 9393);


        log.info("GRPC server started on [::0]:50052");

        server.awaitTermination();
    }
}
