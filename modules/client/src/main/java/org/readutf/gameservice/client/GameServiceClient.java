package org.readutf.gameservice.client;

import game_server.GameServiceGrpc;
import game_server.GameServiceOuterClass;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.client.capacity.CapacitySupplier;
import org.readutf.gameservice.client.exception.GameServiceException;
import org.readutf.gameservice.client.platform.ContainerResolver;
import org.readutf.gameservice.client.platform.DockerResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServiceClient {

    private static final Logger log = LoggerFactory.getLogger(GameServiceClient.class);

    private final @NotNull GameServiceGrpc.GameServiceFutureStub futureStub;
    private final @NotNull GameServiceGrpc.GameServiceStub asyncStub;
    private final @NotNull ScheduledExecutorService executor;
    private final @NotNull ManagedChannel channel;
    private final @NotNull CountDownLatch heartbeatLatch = new CountDownLatch(1);
    private final @NotNull ContainerResolver containerResolver;
    private final @NotNull CapacitySupplier capacitySupplier;
    private final @NotNull List<String> tags;

    GameServiceClient(
            @NotNull String uri,
            @NotNull ContainerResolver containerResolver,
            @NotNull CapacitySupplier capacitySupplier,
            @NotNull List<String> tags) {
        this.channel =
                Grpc.newChannelBuilder(uri, InsecureChannelCredentials.create()).build();
        this.futureStub = GameServiceGrpc.newFutureStub(channel);
        this.asyncStub = GameServiceGrpc.newStub(channel);
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.tags = tags;
        this.containerResolver = containerResolver;
        this.capacitySupplier = capacitySupplier;
    }

    @Blocking
    boolean startBlocking() throws InterruptedException {
        UUID serverId = register(containerResolver.getContainerId(), tags);

        log.info("GameServiceClient started with serverId: {}", serverId);

        startHeartbeat(serverId);

        log.info("GameServiceClient finished with serverId: {}", serverId);

        shutdown();

        return true;
    }

    void disconnect() {
        heartbeatLatch.countDown();
    }

    private void shutdown() throws InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
        channel.awaitTermination(1, TimeUnit.SECONDS);
    }

    private UUID register(String container, List<String> tags) throws GameServiceException {
        try {
            GameServiceOuterClass.RegisterRequest request = GameServiceOuterClass.RegisterRequest.newBuilder()
                    .addAllTags(tags)
                    .setContainerId(container)
                    .build();
            GameServiceOuterClass.RegisterResponse registerResponse =
                    futureStub.register(request).get(15, TimeUnit.SECONDS);
            return UUID.fromString(registerResponse.getId());
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new GameServiceException("Failed to register container", e);
        }
    }

    @Blocking
    private void startHeartbeat(UUID serverId) throws InterruptedException {
        var heartbeatSender = asyncStub.heartbeat(new HeartbeatObserver(heartbeatLatch));

        ScheduledFuture<?> future = executor.scheduleAtFixedRate(
                () -> {
                    float capacity = capacitySupplier.getCapacity();

                    GameServiceOuterClass.HeartbeatRequest heartbeatRequest =
                            GameServiceOuterClass.HeartbeatRequest.newBuilder()
                                    .setServerId(serverId.toString())
                                    .setCapacity(capacity)
                                    .build();

                    heartbeatSender.onNext(heartbeatRequest);
                },
                0,
                1,
                TimeUnit.SECONDS);

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
            String uri = host + ":" + port;
            return new ReconnectingGameService(() -> new GameServiceClient(uri, resolver, capacitySupplier, tags));
        }
    }
}
