package org.readutf.gameservice.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.common.Game;
import org.readutf.gameservice.common.Heartbeat;
import org.readutf.gameservice.common.Server;

public class ServerManager {

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

    public UUID registerServer(String containerId) throws ServerException {
        if (getServerByContainer(containerId) != null) {
            throw new ServerException("Server with container ID " + containerId + " already exists.");
        }
        Server server = new Server(UUID.randomUUID(), containerId, new Heartbeat(System.currentTimeMillis(), 0));
        servers.add(server);
        return server.getServerId();
    }

    public void handleHeartbeat(@NotNull UUID uuid, float capacity, @NotNull List<@NotNull Game> games) throws ServerException {
        Server serverById = getServerById(uuid);
        if (serverById == null) {
            throw new ServerException("Server with ID " + uuid + " not found.");
        }
        serverById.setHeartbeat(new Heartbeat(System.currentTimeMillis(), capacity));
        serverById.setGames(games);
    }
}
