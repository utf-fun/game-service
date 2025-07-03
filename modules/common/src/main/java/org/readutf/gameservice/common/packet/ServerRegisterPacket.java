package org.readutf.gameservice.common.packet;


import org.readutf.hermes.packet.Packet;

import java.util.List;
import java.util.UUID;

public class ServerRegisterPacket extends Packet<UUID> {

    private final String containerId;
    private final List<String> tags;
    private final List<String> playlists;

    public ServerRegisterPacket(String containerId, List<String> tags, List<String> playlists) {
        super(false);
        this.containerId = containerId;
        this.tags = tags;
        this.playlists = playlists;
    }

    public String getContainerId() {
        return containerId;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getPlaylists() {
        return playlists;
    }
}
