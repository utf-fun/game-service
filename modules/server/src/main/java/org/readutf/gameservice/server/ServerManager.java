package org.readutf.gameservice.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.readutf.gameservice.common.Game;
import org.readutf.gameservice.common.Heartbeat;
import org.readutf.gameservice.common.container.ContainerInfo;
import org.readutf.gameservice.common.container.NetworkSettings;
import org.readutf.gameservice.common.Server;
import org.readutf.gameservice.container.ContainerPlatform;
import org.readutf.gameservice.game.GameResult;
import org.readutf.hermes.platform.HermesChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerManager {

    private static final Logger logger = LoggerFactory.getLogger(ServerManager.class);

    private final ContainerPlatform<?> containerPlatform;
    private final List<Server> servers;
    private final ConcurrentHashMap<UUID, HermesChannel> serverChannels;

    public ServerManager(ContainerPlatform<?> containerPlatform) {
        this.containerPlatform = containerPlatform;
        this.servers = new ArrayList<>();
        this.serverChannels = new ConcurrentHashMap<>();
    }

    public UUID registerServer(HermesChannel hermesChannel, String shortContainerId, List<String> tags, List<String> playlists) throws ServerException {
        @Nullable ContainerInfo containerInfo = containerPlatform.getContainerInfo(shortContainerId);
        if (containerInfo == null) {
            logger.error("Network settings for container ID {} not found.", shortContainerId);
            throw new ServerException("Network settings for container ID " + shortContainerId + " not found.");
        }
        if (getServerByContainer(containerInfo.getContainerId()) != null) {
            logger.error("Server with ID {} already exists.", shortContainerId);
            throw new ServerException("Server with container ID " + shortContainerId + " already exists.");
        }

        Server server = new Server(
                UUID.randomUUID(),
                containerInfo.getContainerId(),
                containerInfo.getNetworkSettings(),
                new Heartbeat(System.currentTimeMillis(), 0),
                tags,
                playlists
        );
        servers.add(server);
        serverChannels.put(server.getServerId(), hermesChannel);

        logger.info("Registering server '{}' (Tags: {})", shortContainerId, String.join(", ", tags));
        return server.getServerId();
    }

    public void unregisterServer(@NotNull UUID serverId) {
        Server server = getServerById(serverId);
        if (server == null) {
            logger.error("Server with ID {} not found.", serverId);
            return;
        }
        servers.remove(server);
        serverChannels.remove(serverId);
        logger.info("Server with ID {} unregistered successfully.", serverId);
    }

    public void handleHeartbeat(@NotNull UUID uuid, float capacity, @NotNull List<@NotNull Game> games)
            throws ServerException {
        logger.debug("Handling heartbeat for server ID: {}", uuid);
        Server serverById = getServerById(uuid);
        if (serverById == null) {
            throw new ServerException("Server with ID " + uuid + " not found.");
        }
        serverById.setHeartbeat(new Heartbeat(System.currentTimeMillis(), capacity));
    }

    public Server getServerByContainer(String containerId) {
        for (Server server : servers) {
            if (server.getContainerId().equals(containerId)) {
                return server;
            }
        }
        return null;
    }

    public @Nullable HermesChannel getServerChannelById(@NotNull UUID serverId) {
        return serverChannels.get(serverId);
    }

    public Server getServerById(UUID serverId) {
        for (Server server : servers) {
            if (server.getServerId().equals(serverId)) {
                return server;
            }
        }
        return null;
    }

    public List<Server> getActiveServers() {
        List<Server> activeServers = new ArrayList<>();
        for (Server server : servers) {
            if (System.currentTimeMillis() - server.getHeartbeat().getTimestamp() < 15_000) { // 15 seconds
                activeServers.add(server);
            }
        }
        return activeServers;
    }
}
