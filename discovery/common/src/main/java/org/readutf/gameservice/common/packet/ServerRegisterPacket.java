package org.readutf.gameservice.common.packet;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.readutf.hermes.packet.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerRegisterPacket extends Packet<UUID> {

    private final @Nullable UUID previousId;
    private final @NotNull String containerId;
    private final @NotNull List<String> tags;
    private final @NotNull List<String> playlists;

    public ServerRegisterPacket(@NotNull String containerId, @NotNull List<String> tags, @NotNull List<String> playlists) {
        this(null, containerId, tags, playlists);
    }

    public ServerRegisterPacket(@Nullable UUID previousId, @NotNull String containerId, @NotNull List<String> tags, @NotNull List<String> playlists) {
        super(true);
        this.previousId = previousId;
        this.containerId = containerId;
        this.tags = new ArrayList<>(tags);
        this.playlists = new ArrayList<>(playlists);
    }

    public @Nullable UUID getPreviousId() {
        return previousId;
    }

    public @NotNull String getContainerId() {
        return containerId;
    }

    public @NotNull List<String> getTags() {
        return tags;
    }

    public @NotNull List<String> getPlaylists() {
        return playlists;
    }
}
