package org.readutf.gameservice.social.session;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class SessionManager {

    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

    // Maximum time (in ms) a player can be considered online after last update
    private static final long MAX_OFFLINE_DELAY = 15_000;
    // Debounce time (in ms) to delay marking a player as offline after disconnect
    private static final long DISCONNECT_DEBOUNCE = 5_000;

    // Tracks player statuses by their UUID
    private static final @NotNull Map<UUID, PlayerSession> playerStatusTracker = new ConcurrentHashMap<>();

    static {
        SCHEDULER.scheduleAtFixedRate(SessionManager::clearStalePlayers, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * Called when a player joins a server.
     *
     * @param playerId The player's UUID
     * @param serverId The server's UUID
     */
    public static void onJoin(UUID playerId, UUID serverId) {
        playerStatusTracker.put(playerId, new PlayerSession(System.currentTimeMillis(), serverId));
    }

    /**
     * Called when a player leaves a server.
     *
     * @param serverId The server's UUID
     * @param playerId The player's UUID
     */
    public static void onLeave(UUID serverId, UUID playerId) {
        // Get current status or create a new one if not present
        PlayerSession status =
                playerStatusTracker.getOrDefault(playerId, new PlayerSession(System.currentTimeMillis(), serverId));

        // Ignore if the leave event is for a different server
        if (status.getServerId() != serverId) {
            log.warn("Received out of order leave");
            return;
        }

        // Set last update time with debounce to delay offline marking
        status.setLastUpdate(System.currentTimeMillis() + (DISCONNECT_DEBOUNCE));
    }

    public static void serverShutdown(UUID serverId) {
        synchronized (playerStatusTracker) {
            for (Map.Entry<UUID, PlayerSession> uuidPlayerStatusEntry : playerStatusTracker.entrySet()) {
                if (uuidPlayerStatusEntry.getValue().getServerId() == serverId) {
                    playerStatusTracker.remove(uuidPlayerStatusEntry.getKey());
                }
            }
        }
    }

    /**
     * Checks if a player is considered online.
     *
     * @param playerId The player's UUID
     * @return true if online, false otherwise
     */
    public static boolean isOnline(UUID playerId) {
        PlayerSession status = playerStatusTracker.get(playerId);

        if (status == null) return false;
        // Remove player if offline delay exceeded
        if (status.getLastUpdate() + MAX_OFFLINE_DELAY < System.currentTimeMillis()) {
            playerStatusTracker.remove(playerId);
        }

        return false;
    }

    private static void clearStalePlayers() {
        synchronized (playerStatusTracker) {
            for (Map.Entry<UUID, PlayerSession> entry : playerStatusTracker.entrySet()) {
                if (entry.getValue().getLastUpdate() + MAX_OFFLINE_DELAY < System.currentTimeMillis()) {
                    playerStatusTracker.remove(entry.getKey());
                }
            }
        }
    }

    public static @Nullable PlayerSession getSession(UUID playerId) {
        return playerStatusTracker.get(playerId);
    }

    @NotNull
    public static List<PlayerSession> getAllSessions() {
        return List.copyOf(playerStatusTracker.values());
    }
}
