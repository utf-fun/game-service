package org.readutf.gameservice.grpc;

import game_server.GameServiceGrpc;
import game_server.GameServiceOuterClass;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.Collections;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import org.readutf.gameservice.server.ServerException;
import org.readutf.gameservice.server.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameService extends GameServiceGrpc.GameServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(GameService.class);
    private final ServerManager serverManager;

    public GameService(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    @Override
    public void register(GameServiceOuterClass.RegisterRequest request, StreamObserver<GameServiceOuterClass.RegisterResponse> responseObserver) {
        UUID serverId;
        try {
            serverId = serverManager.registerServer(request.getContainerId(), request.getTagsList());
        } catch (ServerException e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asException());
            return;
        }
        responseObserver.onNext(GameServiceOuterClass.RegisterResponse.newBuilder()
                .setId(serverId.toString())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<GameServiceOuterClass.HeartbeatRequest> heartbeat(StreamObserver<GameServiceOuterClass.HeartbeatResponse> responseObserver) {
        return new StreamObserver<>() {

            private @Nullable UUID serverId = null;

            @Override
            public void onNext(GameServiceOuterClass.HeartbeatRequest value) {

                try {
                    this.serverId = UUID.fromString(value.getServerId());
                    serverManager.handleHeartbeat(serverId, value.getCapacity(), Collections.emptyList());
                } catch (ServerException | IllegalArgumentException e) {
                    responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asException());
                }
            }

            @Override
            public void onError(Throwable t) {
                if(serverId != null) {
                    serverManager.unregisterServer(serverId);
                }
            }

            @Override
            public void onCompleted() {
                log.info("Heartbeat stream completed");
            }
        };
    }
}
