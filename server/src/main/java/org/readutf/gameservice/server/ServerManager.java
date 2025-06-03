package org.readutf.gameservice.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.readutf.gameservice.common.Game;
import org.readutf.gameservice.common.Heartbeat;
import org.readutf.gameservice.common.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerManager {

    private static final Logger logger = LoggerFactory.getLogger(ServerManager.class);

    private final List<Server> servers;

    public ServerManager() {
        this.servers = new ArrayList<>();
    }

    public Server getServerByContainer(String containerId) {
        for (Server server : servers) {
            if (server.getContainerId().equals(containerId)) {
                return server;
            }
        }
        return null;
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

    public UUID registerServer(String containerId) throws ServerException {
        logger.info("Registering server with container ID: {}", containerId);
        if (getServerByContainer(containerId) != null) {
            logger.error("Server with ID {} already exists.", containerId);
            throw new ServerException("Server with container ID " + containerId + " already exists.");
        }
        Server server = new Server(UUID.randomUUID(), containerId, new Heartbeat(System.currentTimeMillis(), 0));
        servers.add(server);
        return server.getServerId();
    }

    public void unregisterServer(@NotNull UUID serverId) {
        logger.info("Unregistering server with ID: {}", serverId);
        Server server = getServerById(serverId);
        if (server == null) {
            logger.error("Server with ID {} not found.", serverId);
            return;
        }
        servers.remove(server);
        logger.info("Server with ID {} unregistered successfully.", serverId);
    }

    public void handleHeartbeat(@NotNull UUID uuid, float capacity, @NotNull List<@NotNull Game> games) throws ServerException {
        logger.debug("Handling heartbeat for server ID: {}", uuid);
        Server serverById = getServerById(uuid);
        if (serverById == null) {
            throw new ServerException("Server with ID " + uuid + " not found.");
        }
        serverById.setHeartbeat(new Heartbeat(System.currentTimeMillis(), capacity));
        serverById.setGames(games);
    }
}
