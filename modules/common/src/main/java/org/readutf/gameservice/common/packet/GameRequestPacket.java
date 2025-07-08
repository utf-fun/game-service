package org.readutf.gameservice.common.packet;

import org.jetbrains.annotations.NotNull;
import org.readutf.hermes.packet.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameRequestPacket extends Packet<UUID> {

    @NotNull
    private final String playlist;
    
    @NotNull
    private final List<List<UUID>> teams;

    public GameRequestPacket(@NotNull String playlist, @NotNull List<List<UUID>> teams) {
        super(true);
        this.playlist = playlist;
        this.teams = new ArrayList<>(teams.stream().map(ArrayList::new).toList());
    }

    public @NotNull String getPlaylist() {
        return playlist;
    }

    public @NotNull List<List<UUID>> getTeams() {
        return teams;
    }
}
