package org.readutf.gameservice.common;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.common.container.NetworkSettings;

public class Server {

    private @NotNull final UUID serverId;
    private @NotNull final String containerId;
    private @NotNull final NetworkSettings networkSettings;
    private @NotNull Heartbeat heartbeat;
    private @NotNull List<String> tags;
    private @NotNull List<String> playlists;

    public Server(
            @NotNull UUID serverId,
            @NotNull String containerId,
            @NotNull NetworkSettings networkSettings,
            @NotNull Heartbeat heartbeat,
            @NotNull List<String> tags,
            @NotNull List<String> playlists
    ) {
        this.serverId = serverId;
        this.containerId = containerId;
        this.networkSettings = networkSettings;
        this.heartbeat = heartbeat;
        this.tags = tags;
        this.playlists = playlists;
    }

    public @NotNull UUID getServerId() {
        return serverId;
    }

    public @NotNull String getContainerId() {
        return containerId;
    }

    public @NotNull NetworkSettings getNetworkSettings() {
        return networkSettings;
    }

    public @NotNull Heartbeat getHeartbeat() {
        return heartbeat;
    }

    public @NotNull List<String> getTags() {
        return tags;
    }

    public @NotNull List<String> getPlaylists() {
        return playlists;
    }

    public void setHeartbeat(@NotNull Heartbeat heartbeat) {
        this.heartbeat = heartbeat;
    }
}
