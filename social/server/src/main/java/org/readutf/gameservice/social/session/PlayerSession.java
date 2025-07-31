package org.readutf.gameservice.social.session;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class PlayerSession {
    private long lastUpdate;
    private @NotNull final UUID serverId;

    public PlayerSession(long lastUpdate, @NotNull UUID serverId) {
        this.lastUpdate = lastUpdate;
        this.serverId = serverId;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    @NotNull
    public UUID getServerId() {
        return serverId;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
