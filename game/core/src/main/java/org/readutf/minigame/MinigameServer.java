package org.readutf.minigame;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import org.jetbrains.annotations.NotNull;
import org.readutf.minigame.listeners.PlayerConfigListener;
import org.readutf.minigame.production.GameDiscovery;

import java.util.List;
import java.util.UUID;

public abstract class MinigameServer {

    @NotNull
    private final String playlist;

    public MinigameServer(String playlist, boolean production) {
        this.playlist = playlist;
        if(production) {
            new GameDiscovery(this);
        } else {

        }

        MinecraftServer server = MinecraftServer.init();
        server.start("0.0.0.0", 25565);

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, new PlayerConfigListener());
    }

    public abstract UUID start(List<List<UUID>> teams) throws Exception;

    public abstract void cancel(UUID gameId);

    public abstract float getCapacity();

    public @NotNull String getPlaylist() {
        return playlist;
    }
}
