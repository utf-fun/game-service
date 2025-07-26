package org.readutf.lobby.listeners;

import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import org.jetbrains.annotations.NotNull;
import org.readutf.lobby.build.LobbyBuild;

import java.util.function.Consumer;

public class AsyncConfigListener implements Consumer<AsyncPlayerConfigurationEvent> {

    private @NotNull final LobbyBuild lobbyBuild;

    public AsyncConfigListener(@NotNull LobbyBuild lobbyBuild) {
        this.lobbyBuild = lobbyBuild;
    }

    @Override
    public void accept(AsyncPlayerConfigurationEvent asyncPlayerConfigurationEvent) {
        asyncPlayerConfigurationEvent.setSpawningInstance(lobbyBuild.instance());
        asyncPlayerConfigurationEvent.getPlayer().setPermissionLevel(3);
    }
}
