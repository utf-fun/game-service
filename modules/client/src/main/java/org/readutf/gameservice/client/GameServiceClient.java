package org.readutf.gameservice.client;

import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.client.capacity.CapacitySupplier;
import org.readutf.gameservice.client.heartbeat.HeartbeatTask;
import org.readutf.gameservice.client.platform.ContainerResolver;
import org.readutf.gameservice.common.SharedKryo;
import org.readutf.gameservice.common.packet.ServerRegisterPacket;
import org.readutf.hermes.kryo.KryoPacketCodec;
import org.readutf.hermes.netty.NettyClientPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameServiceClient {

    private static final Logger log = LoggerFactory.getLogger(GameServiceClient.class);
    private NettyClientPlatform nettyClient;
    private final ScheduledExecutorService reconnectExecutor = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService heartbeatExecutor;
    private final ContainerResolver containerResolver;
    private final @NotNull List<@NotNull String> tags;
    private final @NotNull CapacitySupplier capacitySupplier;

    public GameServiceClient(ContainerResolver containerResolver, @NotNull List<String> tags, @NotNull CapacitySupplier capacitySupplier) {
        this.containerResolver = containerResolver;
        this.heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
        this.capacitySupplier = capacitySupplier;
        this.tags = new ArrayList<>(tags);
    }

    @Blocking
    public void startBlocking(InetSocketAddress address) {

        heartbeatExecutor.scheduleAtFixedRate(new HeartbeatTask(() -> nettyClient, capacitySupplier), 0, 1, TimeUnit.SECONDS);

        reconnectExecutor.scheduleAtFixedRate(() -> {
            try {
                this.nettyClient = new NettyClientPlatform(new KryoPacketCodec(SharedKryo::createKryo));

                nettyClient.connect(address);

                log.info("Connected to Game Service at {}", address);

                nettyClient.sendPacket(new ServerRegisterPacket(containerResolver.getContainerId(), tags, new ArrayList<>()));

                nettyClient.awaitShutdown();


                this.nettyClient = null;
            } catch (Exception e) {
                log.error("Failed to connect to Game Service, restarting in 5 seconds", e);
            }

        }, 0, 5, TimeUnit.SECONDS);


        log.info("Client shut down");
    }

    public void stop() {
        reconnectExecutor.shutdown();
        if (nettyClient != null) {
            nettyClient.shutdown();
        }
    }

}
