package org.readutf.gameservice.common.packet;


import org.readutf.hermes.packet.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerRegisterPacket extends Packet<UUID> {

    private final String containerId;
    private final List<String> tags;
    private final List<String> playlists;

    public ServerRegisterPacket(String containerId, List<String> tags, List<String> playlists) {
        super(true);
        this.containerId = containerId;
        this.tags = new ArrayList<>(tags);
        this.playlists = new ArrayList<>(playlists);
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
