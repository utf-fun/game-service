package org.readutf.gameservice.proxy.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.api.GameServiceApi;
import org.readutf.gameservice.common.Server;
import org.readutf.gameservice.exceptions.GameServiceException;
import org.readutf.gameservice.proxy.DiscoveryProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PreConnectListener {

    private static final Logger logger = LoggerFactory.getLogger(PreConnectListener.class);

    private final @NotNull DiscoveryProxy proxy;
    private final @NotNull GameServiceApi gameServiceApi;

    public PreConnectListener(@NotNull DiscoveryProxy proxy, @NotNull GameServiceApi gameServiceApi) {
        this.proxy = proxy;
        this.gameServiceApi = gameServiceApi;
    }

    @Subscribe
    public void onPreJoin(@NotNull PlayerChooseInitialServerEvent event) {
        logger.info(
                "Player {} is attempting to connect to a server.",
                event.getPlayer().getUsername());

        List<Server> servers;
        try {
            servers = gameServiceApi.getServersByTag("lobby");
        } catch (GameServiceException e) {
            logger.error("Failed to fetch servers from Game Service: {}", e.getMessage());
            return;
        }

        Optional<Server> bestServer = servers.stream().min(Comparator.comparingInt(server1 ->
                (int) server1.getHeartbeat().getCapacity()));

        if (bestServer.isEmpty()) {
            event.getPlayer()
                    .disconnect(Component.text("No lobby servers available at the moment. Please try again later."));
        } else {
            Server server = bestServer.get();
            logger.info("Redirecting player to server: {}", server.getServerId());
            event.setInitialServer(proxy.getServer(server));
        }
    }
}
