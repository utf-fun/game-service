package org.readutf.gameservice.api;

import game_server.GameServiceGrpc;
import game_server.GameServiceOuterClass;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.readutf.gameservice.common.Game;
import org.readutf.gameservice.server.ServerException;
import org.readutf.gameservice.server.ServerManager;

public class GameServiceEndpoint extends GameServiceGrpc.GameServiceImplBase {

    private final ServerManager serverManager;

    public GameServiceEndpoint(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    @Override
    public void register(GameServiceOuterClass.RegisterRequest request, StreamObserver<GameServiceOuterClass.RegisterResponse> responseObserver) {
        UUID serverId;
        try {
            serverId = serverManager.registerServer(request.getContainerId());
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
            @Override
            public void onNext(GameServiceOuterClass.HeartbeatRequest value) {
                List<GameServiceOuterClass.Game> protoGames = value.getGamesList();

                List<Game> games = new ArrayList<>();
                for (GameServiceOuterClass.Game protoGame : protoGames) {
                    Game game = new Game(
                            UUID.fromString(protoGame.getId()),
                            protoGame.getPlaylist(),
                            protoGame.getActive()
                    );
                    games.add(game);
                }

                try {
                    UUID serverId = UUID.fromString(value.getServerId());
                    serverManager.handleHeartbeat(serverId, value.getCapacity(), games);
                } catch (ServerException | IllegalArgumentException e) {
                    responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asException());
                }
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {

            }
        };
    }

    @Override
    public void unRegister(GameServiceOuterClass.UnregisterRequest request, StreamObserver<GameServiceOuterClass.UnregisterResponse> responseObserver) {
        super.unRegister(request, responseObserver);
    }
}
