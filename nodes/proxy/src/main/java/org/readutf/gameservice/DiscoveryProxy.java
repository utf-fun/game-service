package org.readutf.gameservice;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.readutf.gameservice.common.Server;
import org.readutf.gameservice.listeners.PluginMessageListener;
import org.readutf.gameservice.listeners.PreConnectListener;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Plugin(name = "Discovery", id = "discovery", version = "1.0.0", authors = "utf_")
public class DiscoveryProxy {

    private final ProxyServer proxy;
    private final Logger logger;
    private final GameServiceApi gameServiceApi;
    private final @NotNull HashMap<UUID, RegisteredServer> serverCache;
    private final @NotNull AtomicInteger serverCounter;

    @Inject
    public DiscoveryProxy(ProxyServer proxy, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;
        this.gameServiceApi = new GameServiceApi(System.getenv("GAME_SERVICE_URL"));
        this.serverCache = new HashMap<>();
        this.serverCounter = new AtomicInteger(0);

        logger.info("DiscoveryProxy initialized with Game Service URL: {}", System.getenv("GAME_SERVICE_URL"));
    }
    
    @Subscribe
    public void onInit(ProxyInitializeEvent event) {
        logger.info("Initializing DiscoveryProxy...");

        proxy.getEventManager().register(this, new PreConnectListener(this, gameServiceApi));
        proxy.getEventManager().register(this, new PluginMessageListener(this, gameServiceApi));
    }

    public @Nullable RegisteredServer getServer(@NotNull UUID serverId) {
        return serverCache.get(serverId);
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
