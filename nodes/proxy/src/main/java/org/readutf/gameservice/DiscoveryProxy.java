package org.readutf.gameservice;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import net.kyori.adventure.text.Component;
import org.readutf.gameservice.common.Server;
import org.readutf.gameservice.exceptions.GameServiceException;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Plugin(name = "Discovery", id = "discovery", version = "1.0.0", authors = "utf_")
public class DiscoveryProxy {

    private final ProxyServer proxy;
    private final Logger logger;
    private final GameServiceApi gameServiceApi;

    private final HashMap<UUID, RegisteredServer> serverCache = new HashMap<>();
    private final AtomicInteger serverCounter = new AtomicInteger(0);

    @Inject
    public DiscoveryProxy(ProxyServer proxy, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;
        this.gameServiceApi = new GameServiceApi(System.getenv("GAME_SERVICE_URL"));

        logger.info("DiscoveryProxy initialized with Game Service URL: {}", System.getenv("GAME_SERVICE_URL"));

    }

    @Subscribe
    public void onPreJoin(ServerPreConnectEvent event) {
        logger.info("Player {} is attempting to connect to a server.", event.getPlayer().getUsername());

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
            event.setResult(ServerPreConnectEvent.ServerResult.denied());
            event.getPlayer()
                    .disconnect(Component.text("No lobby servers available at the moment. Please try again later."));
        } else {
            Server server = bestServer.get();
            logger.info("Redirecting player to server: {}", server.getServerId());
            event.setResult(ServerPreConnectEvent.ServerResult.allowed(getServer(server)));
        }
    }

    public RegisteredServer getServer(Server server) {
        RegisteredServer cachedServer = serverCache.get(server.getServerId());
        if (cachedServer != null) {
            return cachedServer;
        }

        String hostname = server.getContainerInfo().getNetworkSettings().hostname();

        ServerInfo serverInfo =
                new ServerInfo("dynamic-" + serverCounter.incrementAndGet(), new InetSocketAddress(hostname, 25565));

        RegisteredServer registeredServer = proxy.registerServer(serverInfo);
        serverCache.put(server.getServerId(), registeredServer);
        return registeredServer;
    }
}
