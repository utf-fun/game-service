package org.readutf.gameservice.listeners;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.readutf.gameservice.common.packet.HeartbeatPacket;
import org.readutf.gameservice.common.packet.ServerRegisterPacket;
import org.readutf.gameservice.server.ServerException;
import org.readutf.gameservice.server.ServerManager;
import org.readutf.hermes.platform.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscoveryService {

    private static final Logger log = LoggerFactory.getLogger(DiscoveryService.class);
    private final ServerManager serverManager;
    private final Map<Channel, UUID> channelToServerIdMap = new ConcurrentHashMap<>();

    public DiscoveryService(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    public UUID onRegister(Channel channel, ServerRegisterPacket packet) throws ServerException {
        UUID serverId = serverManager.registerServer(packet.getContainerId(), packet.getTags());
        channelToServerIdMap.put(channel, serverId);
        return serverId;
    }

    public void onHeartbeat(Channel channel, HeartbeatPacket heartbeatPacket) throws ServerException {
        UUID serverId = channelToServerIdMap.get(channel);
        if (serverId == null) {
            throw new ServerException("Server not registered");
        }
        serverManager.handleHeartbeat(serverId, heartbeatPacket.getCapacity(), Collections.emptyList());
    }
}
