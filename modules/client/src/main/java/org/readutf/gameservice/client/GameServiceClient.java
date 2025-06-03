package org.readutf.gameservice.client;

import game_server.GameServiceGrpc;
import game_server.GameServiceOuterClass;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.jetbrains.annotations.Blocking;
import org.readutf.gameservice.client.exception.GameServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServiceClient {

    private static final Logger log = LoggerFactory.getLogger(GameServiceClient.class);
    private final GameServiceGrpc.GameServiceFutureStub futureStub;
    private final GameServiceGrpc.GameServiceStub asyncStub;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final ManagedChannel channel;
    private final CountDownLatch heartbeatLatch = new CountDownLatch(1);

    private GameServiceClient() {
        this.channel = Grpc.newChannelBuilder("[::1]:50052", InsecureChannelCredentials.create()).build();
        this.futureStub = GameServiceGrpc.newFutureStub(channel);
        this.asyncStub = GameServiceGrpc.newStub(channel);
    }

    @Blocking
    private void startBlocking() throws InterruptedException {
        UUID serverId = register(String.valueOf(System.currentTimeMillis()));

        log.info("GameServiceClient started with serverId: {}", serverId);

        startHeartbeat(serverId);

        log.info("GameServiceClient finished with serverId: {}", serverId);

        shutdown();
    }

    private void disconnect() {
        heartbeatLatch.countDown();
    }

    private void shutdown() throws InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
        channel.awaitTermination(1, TimeUnit.SECONDS);
    }


    private UUID register(String container) throws GameServiceException {
        try {
            GameServiceOuterClass.RegisterResponse registerResponse = futureStub.register(GameServiceOuterClass.RegisterRequest.newBuilder().setContainerId(container).build()).get(15, TimeUnit.SECONDS);
            return UUID.fromString(registerResponse.getId());
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new GameServiceException("Failed to register container", e);
        }
    }

    @Blocking
    private void startHeartbeat(UUID serverId) throws InterruptedException {
        var heartbeatSender = asyncStub.heartbeat(new HeartbeatObserver(heartbeatLatch));

        ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> {
            GameServiceOuterClass.HeartbeatRequest heartbeatRequest = GameServiceOuterClass.HeartbeatRequest.newBuilder()
                    .setServerId(serverId.toString())
                    .setCapacity(0)
                    .build();

            heartbeatSender.onNext(heartbeatRequest);
        }, 0, 1, TimeUnit.SECONDS);

        heartbeatLatch.await();
        future.cancel(true);
    }

    public static void main(String[] args) throws InterruptedException {
        ReconnectingGameService reconnectingGameService = new ReconnectingGameService();
        Thread thread = new Thread(reconnectingGameService);
        thread.setDaemon(false);
        thread.start();

        Thread.sleep(5000);

        reconnectingGameService.shutdown();
    }

    public static class ReconnectingGameService implements Runnable {

        private GameServiceClient client;
        private boolean active = true;

        @Override
        public void run() {
            while (active) {
                try {
                    client = new GameServiceClient();
                    client.startBlocking();
                    client = null;
                } catch (GameServiceException e) {
                    log.error("Error in GameServiceClient", e);
                    try {
                        Thread.sleep(1000); // Retry after 1 second
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public void shutdown() {
            active = false;
            if(client != null) {
                client.disconnect();
                log.info("Shut down GameServiceClient");
            }
        }

    }

}
