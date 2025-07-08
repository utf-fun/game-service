package org.readutf.gameservice.client;

import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.client.capacity.CapacitySupplier;
import org.readutf.gameservice.client.game.GameRequestHandler;
import org.readutf.gameservice.client.heartbeat.HeartbeatTask;
import org.readutf.gameservice.client.platform.ContainerResolver;
import org.readutf.gameservice.common.SharedKryo;
import org.readutf.gameservice.common.packet.GameRequestPacket;
import org.readutf.gameservice.common.packet.ServerRegisterPacket;
import org.readutf.hermes.kryo.KryoPacketCodec;
import org.readutf.hermes.netty.NettyClientPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameServiceClient {

    private static final Logger log = LoggerFactory.getLogger(GameServiceClient.class);
    private NettyClientPlatform nettyClient;

    @NotNull
    private final ScheduledExecutorService reconnectExecutor = Executors.newSingleThreadScheduledExecutor();

    @NotNull
    private final ScheduledExecutorService heartbeatExecutor;

    @NotNull
    private final ContainerResolver containerResolver;

    @NotNull
    private final CapacitySupplier capacitySupplier;

    @NotNull
    private final GameRequestHandler requestHandler;

    @NotNull
    private final List<@NotNull String> tags;

    private final List<@NotNull String> playlists;

    private GameServiceClient(
            @NotNull ContainerResolver containerResolver,
            @NotNull List<String> tags,
            @NotNull CapacitySupplier capacitySupplier,
            @NotNull GameRequestHandler requestHandler,
            List<@NotNull String> playlists) {
        this.containerResolver = containerResolver;
        this.requestHandler = requestHandler;
        this.heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
        this.capacitySupplier = capacitySupplier;
        this.playlists = new ArrayList<>(playlists);
        this.tags = new ArrayList<>(tags);
    }

    @Blocking
    public void startBlocking(InetSocketAddress address) {

        heartbeatExecutor.scheduleAtFixedRate(
                new HeartbeatTask(() -> nettyClient, capacitySupplier), 0, 1, TimeUnit.SECONDS);

        reconnectExecutor.scheduleAtFixedRate(
                () -> {
                    try {
                        this.nettyClient = new NettyClientPlatform(new KryoPacketCodec(SharedKryo::createKryo));

                        nettyClient.connect(address);

                        log.info("Connected to Game Service at {}", address);

                        nettyClient.sendPacket(
                                new ServerRegisterPacket(containerResolver.getContainerId(), tags, playlists));

                        nettyClient.listen(
                                GameRequestPacket.class,
                                (hermesChannel, packet) ->
                                        requestHandler.requestGame(packet.getPlaylist(), packet.getTeams()));

                        nettyClient.awaitShutdown();

                        this.nettyClient = null;
                    } catch (Exception e) {
                        log.error("Failed to connect to Game Service, restarting in 5 seconds", e);
                    }
                },
                0,
                5,
                TimeUnit.SECONDS);

        log.info("Client shut down");
    }

    public void stop() {
        reconnectExecutor.shutdown();
        if (nettyClient != null) {
            nettyClient.shutdown();
        }
    }

    public static Builder builder(@NotNull ContainerResolver containerResolver) {
        return new Builder(containerResolver);
    }

    public static class Builder {
        private final ContainerResolver containerResolver;
        private final List<String> tags = new ArrayList<>();
        private CapacitySupplier capacitySupplier = () -> 0.0f; // Default capacity supplier
        private GameRequestHandler requestHandler = (playlist, teams) -> {
            throw new UnsupportedOperationException("No request handler set");
        };
        private List<String> playlists = new ArrayList<>();

        public Builder(@NotNull ContainerResolver containerResolver) {
            this.containerResolver = containerResolver;
        }

        public Builder setTags(List<String> tags) {
            this.tags.clear();
            this.tags.addAll(tags);
            return this;
        }

        public Builder setTags(String... tags) {
            this.tags.clear();
            this.tags.addAll(Arrays.asList(tags));
            return this;
        }

        public Builder setCapacitySupplier(@NotNull CapacitySupplier capacitySupplier) {
            this.capacitySupplier = capacitySupplier;
            return this;
        }

        public Builder setRequestHandler(@NotNull GameRequestHandler requestHandler) {
            this.requestHandler = requestHandler;
            return this;
        }

        public Builder setPlaylists(List<String> playlists) {
            this.playlists = playlists;
            return this;
        }

        public GameServiceClient build() {
            return new GameServiceClient(containerResolver, tags, capacitySupplier, requestHandler, playlists);
        }
    }
}
