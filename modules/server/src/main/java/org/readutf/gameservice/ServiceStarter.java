package org.readutf.gameservice;

import com.esotericsoftware.kryo.Kryo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import io.javalin.Javalin;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.api.RouteLogger;
import org.readutf.gameservice.api.routes.GetServerEndpoint;
import org.readutf.gameservice.api.routes.ListServersEndpoint;
import org.readutf.gameservice.common.packet.HeartbeatPacket;
import org.readutf.gameservice.container.ContainerPlatform;
import org.readutf.gameservice.container.docker.DockerContainerPlatform;
import org.readutf.gameservice.container.kubernetes.KubernetesPlatform;
import org.readutf.gameservice.server.ServerManager;
import org.readutf.hermes.kryo.KryoPacketCodec;
import org.readutf.hermes.nio.NioServerPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceStarter {

    private static final Logger log = LoggerFactory.getLogger(ServiceStarter.class);

    private final @NotNull AtomicBoolean running = new AtomicBoolean(true);

    public ServiceStarter() throws Exception {

        String DOCKER_URL = System.getenv("DOCKER_URL");
        String KUBERNETES_URL = System.getenv("KUBERNETES_URL");


        ContainerPlatform<?> platform;
        if (DOCKER_URL != null && !DOCKER_URL.isEmpty()) {
            log.info("Using Docker platform with URL: {}", DOCKER_URL);
            platform = new DockerContainerPlatform(DOCKER_URL);
        } else if (KUBERNETES_URL != null && !KUBERNETES_URL.isEmpty()) {
            log.info("Using Kubernetes platform with URL: {}", KUBERNETES_URL);
            platform = new KubernetesPlatform(KUBERNETES_URL, System.getenv("KUBERNETES_TOKEN"));
        } else {
            throw new IllegalStateException("No valid container platform configured. Set DOCKER_URL or KUBERNETES_URL environment variable.");
        }


        var serverManager = new ServerManager(platform);

        KryoPacketCodec codec = new KryoPacketCodec(() -> {
            Kryo kryo = new Kryo();

            kryo.register(HeartbeatPacket.class);

            return kryo;
        });

        NioServerPlatform server = new NioServerPlatform(codec);
        server.start(new InetSocketAddress("0.0.0.0", 50052));

        Javalin.create(config -> config.showJavalinBanner = false)
                .after(new RouteLogger())
                .get("/api/v1/server", new ListServersEndpoint(serverManager))
                .get("/api/v1/server/{id}", new GetServerEndpoint(serverManager))
                .start("0.0.0.0", 9393);


        log.info("GRPC server started on [::0]:50052");


        Thread keepAliveThread = new Thread(() -> {
            while (running.get()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Service-KeepAlive");

        keepAliveThread.setDaemon(true);
        keepAliveThread.start();
    }

    public static void main(String[] args) throws Exception {
        log.info("Starting server...");
        new ServiceStarter();
    }
}
