package org.readutf.gameservice.proxy;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.readutf.gameservice.api.GameServiceApi;
import org.readutf.gameservice.common.Server;
import org.readutf.gameservice.proxy.listeners.PluginMessageListener;
import org.readutf.gameservice.proxy.listeners.PreConnectListener;
import org.readutf.gameservice.listeners.SessionListeners;
import org.readutf.social.SocialClient;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Plugin(name = "Discovery", id = "discovery", version = "1.0.0", authors = "utf_")
public class DiscoveryProxy {

    private final ProxyServer proxy;
    private final Logger logger;
    private final GameServiceApi gameServiceApi;
    private final @NotNull HashMap<UUID, RegisteredServer> serverCache;
    private final @NotNull AtomicInteger serverCounter;
    private final @NotNull SocialClient socialClient;

    @Inject
    public DiscoveryProxy(ProxyServer proxy, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;
        String gameServiceUrl = System.getenv("GAME_SERVICE_URL");
        String socialServiceHost = System.getenv("SOCIAL_SERVICE_HOST");
        this.gameServiceApi = new GameServiceApi(gameServiceUrl);
        this.serverCache = new HashMap<>();
        this.serverCounter = new AtomicInteger(0);
        this.socialClient = new SocialClient(socialServiceHost);

        logger.info("DiscoveryProxy initialized: ");
        logger.info("  * Game Service URL: {}", gameServiceUrl);
        logger.info("  * Social Service Host: {}", socialServiceHost);
    }
    
    @Subscribe
    public void onInit(ProxyInitializeEvent event) {
        logger.info("Initializing DiscoveryProxy...");

        proxy.getChannelRegistrar().register(PluginMessageListener.IDENTIFIER);
        proxy.getEventManager().register(this, new PreConnectListener(this, gameServiceApi));
        proxy.getEventManager().register(this, new PluginMessageListener(this, gameServiceApi));
        proxy.getEventManager().register(this, new SessionListeners(socialClient));
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
