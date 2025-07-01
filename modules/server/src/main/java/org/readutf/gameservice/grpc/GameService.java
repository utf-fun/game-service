package org.readutf.gameservice.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.Collections;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import org.readutf.gameservice.proto.DiscoveryServiceGrpc;
import org.readutf.gameservice.proto.DiscoveryServiceOuterClass;
import org.readutf.gameservice.server.ServerException;
import org.readutf.gameservice.server.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameService extends DiscoveryServiceGrpc.DiscoveryServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(GameService.class);
    private final ServerManager serverManager;

    public GameService(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    @Override
    public void register(DiscoveryServiceOuterClass.RegisterRequest request, StreamObserver<DiscoveryServiceOuterClass.RegisterResponse> responseObserver) {
        UUID serverId;
        try {
            serverId = serverManager.registerServer(request.getContainerId(), request.getTagsList());
        } catch (ServerException e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asException());
            return;
        }
        responseObserver.onNext(DiscoveryServiceOuterClass.RegisterResponse.newBuilder()
                .setId(serverId.toString())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<DiscoveryServiceOuterClass.HeartbeatRequest> heartbeat(StreamObserver<DiscoveryServiceOuterClass.HeartbeatResponse> responseObserver) {
        return new StreamObserver<>() {

            private @Nullable UUID serverId = null;

            @Override
            public void onNext(DiscoveryServiceOuterClass.HeartbeatRequest value) {

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
