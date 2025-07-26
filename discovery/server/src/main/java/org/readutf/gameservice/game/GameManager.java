package org.readutf.gameservice.game;

import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.readutf.gameservice.common.Server;
import org.readutf.gameservice.common.packet.GameRequestPacket;
import org.readutf.gameservice.server.ServerManager;
import org.readutf.hermes.Hermes;
import org.readutf.hermes.platform.HermesChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class GameManager {

    private static final Logger log = LoggerFactory.getLogger(GameManager.class);
    private final @NotNull ServerManager serverManager;
    private final @NotNull Hermes hermes;

    public GameManager(@NotNull ServerManager serverManager, @NotNull Hermes hermes) {
        this.serverManager = serverManager;
        this.hermes = hermes;
    }

    @Blocking
    @Nullable
    public GameResult findGame(@NotNull String playlist, List<List<UUID>> teams) {

        List<Server> servers = serverManager.getActiveServers().stream()
                .filter(server -> server.getPlaylists().stream().anyMatch(playlist::equalsIgnoreCase))
                .sorted(Comparator.comparingDouble(server -> server.getHeartbeat().getCapacity()))
                .toList();

        for (Server server : servers) {
            try {
                return requestGame(server, playlist, teams).join();
            } catch (Exception e) {
                log.error("Failed to request game from server {}", server.getServerId(), e);
            }
        }

        return null;
    }

    private CompletableFuture<GameResult> requestGame(@NotNull Server server, String playlist, List<List<UUID>> teams) {
        HermesChannel channel = serverManager.getServerChannelById(server.getServerId());
        if (channel == null)
            return CompletableFuture.failedFuture(new IllegalStateException("Server channel not found for server ID: " + server.getServerId()));

        log.info("Requesting game from server {} (Playlist: {}, Teams: {})", server.getServerId(), playlist, teams);
        return hermes.sendResponsePacket(channel, new GameRequestPacket(playlist, teams), UUID.class).thenApply(uuid -> new GameResult(uuid, server));
    }

}
