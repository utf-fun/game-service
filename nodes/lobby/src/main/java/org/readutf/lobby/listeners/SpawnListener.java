package org.readutf.lobby.listeners;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.player.PlayerSpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.readutf.buildformat.common.markers.Position;
import org.readutf.lobby.build.LobbyBuild;

import java.util.function.Consumer;

public class SpawnListener implements Consumer<PlayerSpawnEvent> {

    private final @NotNull LobbyBuild lobbyBuild;

    public SpawnListener(@NotNull LobbyBuild lobbyBuild) {
        this.lobbyBuild = lobbyBuild;
    }

    @Override
    public void accept(PlayerSpawnEvent e) {
        Position spawnMarker = lobbyBuild.positions().spawn().getTargetPosition();

        Pos spawn = new Pos(spawnMarker.x(), spawnMarker.y(), spawnMarker.z());
        if(e.isFirstSpawn()) {
            e.getPlayer().teleport(spawn.add(0.5, 0, 0.5));
        }
    }
}
