package org.readutf.gameservice.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.*;

import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.client.capacity.CapacitySupplier;
import org.readutf.gameservice.client.exception.GameServiceException;
import org.readutf.gameservice.client.platform.ContainerResolver;
import org.readutf.gameservice.client.platform.DockerResolver;
import org.readutf.gameservice.common.SharedKryo;
import org.readutf.gameservice.common.packet.HeartbeatPacket;
import org.readutf.gameservice.common.packet.ServerRegisterPacket;
import org.readutf.hermes.kryo.KryoPacketCodec;
import org.readutf.hermes.nio.NioClientPlatform;
import org.readutf.hermes.packet.ChannelClosePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServiceClient {

    private static final Logger log = LoggerFactory.getLogger(GameServiceClient.class);

    private final @NotNull InetSocketAddress address;
    private final @NotNull NioClientPlatform client;
    private final @NotNull ScheduledExecutorService executor;
    private final @NotNull CountDownLatch heartbeatLatch = new CountDownLatch(1);
    private final @NotNull ContainerResolver containerResolver;
    private final @NotNull CapacitySupplier capacitySupplier;
    private final @NotNull List<String> tags;

    GameServiceClient(
            @NotNull InetSocketAddress address,
            @NotNull ContainerResolver containerResolver,
            @NotNull CapacitySupplier capacitySupplier,
            @NotNull List<String> tags) throws IOException {
        this.address = address;
        this.client = new NioClientPlatform(new KryoPacketCodec(SharedKryo::createKryo));
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.tags = tags;
        this.containerResolver = containerResolver;
        this.capacitySupplier = capacitySupplier;
    }

    /**
     * Will connect to the game server, register the container, start sending heartbeats,
     * and then block until the connection is closed.
     */
    @Blocking
    void start() throws Exception {
        client.connect(address);

        UUID serverId = register(containerResolver.getContainerId(), tags);

        log.info("GameServiceClient started with serverId: {}", serverId);

        startHeartbeat(serverId);

        log.info("GameServiceClient finished with serverId: {}", serverId);

        shutdown();

        CountDownLatch latch = new CountDownLatch(1);
        client.listen(ChannelClosePacket.class, (channel, packet) -> {
            latch.countDown();
            return null;
        });
        latch.await();
    }

    void disconnect() {
        heartbeatLatch.countDown();
    }

    private void shutdown() throws IOException {
        client.disconnect();
    }

    private UUID register(String container, List<String> tags) throws GameServiceException {
        try {
            CompletableFuture<UUID> response = client.sendResponsePacket(new ServerRegisterPacket(container, tags, Collections.emptyList()), UUID.class);
            return response.join();
        } catch (Exception e) {
            throw new GameServiceException("Failed to register container", e);
        }
    }

    @Blocking
    private void startHeartbeat(UUID serverId) throws InterruptedException {

        ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> {
            try {
                client.sendPacket(new HeartbeatPacket(capacitySupplier.getCapacity()));
            } catch (Exception e) {
                log.error("Failed to send heartbeat packet", e);
            }
        }, 0, 1, TimeUnit.SECONDS);

        heartbeatLatch.await();
        future.cancel(true);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String host = "localhost"; // Default host
        private int port = 50051; // Default port
        private ContainerResolver resolver = new DockerResolver();
        private CapacitySupplier capacitySupplier = () -> 0.5f; // Default capacity supplier
        private List<String> tags = new ArrayList<>();

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setContainerResolver(ContainerResolver resolver) {
            this.resolver = resolver;
            return this;
        }

        public Builder setCapacitySupplier(CapacitySupplier capacitySupplier) {
            this.capacitySupplier = capacitySupplier;
            return this;
        }

        public Builder setTags(Collection<String> tags) {
            this.tags = new ArrayList<>(tags);
            return this;
        }

        public void addTag(String tag) {
            tags.add(tag);
        }

        public ReconnectingGameService build() {
            return new ReconnectingGameService(() -> new GameServiceClient(new InetSocketAddress(host, port), resolver, capacitySupplier, tags));
        }
    }
}
