package org.readutf.gameservice.listeners;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.Nullable;
import org.readutf.gameservice.common.packet.HeartbeatPacket;
import org.readutf.gameservice.common.packet.ServerRegisterPacket;
import org.readutf.gameservice.server.ServerException;
import org.readutf.gameservice.server.ServerManager;
import org.readutf.hermes.packet.ChannelClosePacket;
import org.readutf.hermes.platform.HermesChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscoveryService {

    private static final Logger log = LoggerFactory.getLogger(DiscoveryService.class);
    private final ServerManager serverManager;
    private final Map<HermesChannel, UUID> channelToServerIdMap = new ConcurrentHashMap<>();

    public DiscoveryService(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    public UUID onRegister(HermesChannel channel, ServerRegisterPacket packet) throws ServerException {

        UUID serverId = packet.getPreviousId() == null ? UUID.randomUUID() : packet.getPreviousId();

        serverManager.registerServer(serverId, channel, packet.getContainerId(), packet.getTags(), packet.getPlaylists());
        channelToServerIdMap.put(channel, serverId);
        return serverId;
    }

    public void onHeartbeat(HermesChannel channel, HeartbeatPacket heartbeatPacket) {
        UUID serverId = channelToServerIdMap.get(channel);
        if (serverId == null) {
            log.warn("Received heartbeat from unregistered server: {}", channel.getId());
            return;
        }
        try {
            serverManager.handleHeartbeat(serverId, heartbeatPacket.getCapacity(), Collections.emptyList());
        } catch (ServerException e) {
            log.error("Failed to handle heartbeat for server {}: {}", serverId, e.getMessage(), e);
        }
    }

    public void onChannelClose(HermesChannel channel, ChannelClosePacket packet) {
        @Nullable UUID serverId = channelToServerIdMap.get(channel);
        if (serverId == null) {
            log.warn("Received channel close for unregistered server: {}", channel.getId());
            return;
        }
        channelToServerIdMap.remove(channel);
        serverManager.unregisterServer(serverId);
    }
}
