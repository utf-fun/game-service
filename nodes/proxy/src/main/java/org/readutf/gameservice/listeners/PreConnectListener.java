package org.readutf.gameservice.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.GameServiceApi;
import org.readutf.gameservice.common.Server;
import org.readutf.gameservice.exceptions.GameServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PreConnectListener {

    private static final Logger logger = LoggerFactory.getLogger(PreConnectListener.class);

    private final @NotNull ProxyServer proxy;
    private final @NotNull GameServiceApi gameServiceApi;
    private final @NotNull HashMap<UUID, RegisteredServer> serverCache;
    private final @NotNull AtomicInteger serverCounter;

    public PreConnectListener(@NotNull ProxyServer proxy, @NotNull GameServiceApi gameServiceApi) {
        this.proxy = proxy;
        this.gameServiceApi = gameServiceApi;
        this.serverCache = new HashMap<>();
        this.serverCounter = new AtomicInteger(0);
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
            event.setInitialServer(getServer(server));
        }
    }

    public RegisteredServer getServer(Server server) {
        RegisteredServer cachedServer = serverCache.get(server.getServerId());
        if (cachedServer != null) {
            return cachedServer;
        }

        String hostname = server.getNetworkSettings().hostname();

        ServerInfo serverInfo =
                new ServerInfo("dynamic-" + serverCounter.incrementAndGet(), new InetSocketAddress(hostname, 25565));

        RegisteredServer registeredServer = proxy.registerServer(serverInfo);
        serverCache.put(server.getServerId(), registeredServer);
        return registeredServer;
    }
}
