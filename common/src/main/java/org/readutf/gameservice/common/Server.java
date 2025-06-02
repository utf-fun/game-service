package org.readutf.gameservice.common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class Server {

    @NotNull
    private final UUID serverId;

    @NotNull
    private final String containerId;

    @NotNull
    private Heartbeat heartbeat;

    @NotNull
    private List<Game> games;


    public Server(@NotNull UUID serverId, @NotNull String containerId, @NotNull Heartbeat heartbeat) {
        this.serverId = serverId;
        this.containerId = containerId;
        this.heartbeat = heartbeat;
        this.games = new ArrayList<>();
    }

    public @NotNull UUID getServerId() {
        return serverId;
    }

    public @NotNull String getContainerId() {
        return containerId;
    }

    public @NotNull Heartbeat getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(@NotNull Heartbeat heartbeat) {
        this.heartbeat = heartbeat;
    }

    public @NotNull List<Game> getGames() {
        return games;
    }

    public void setGames(@NotNull List<Game> games) {
        this.games = games;
    }
}
