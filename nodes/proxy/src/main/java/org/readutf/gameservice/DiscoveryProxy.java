package org.readutf.gameservice;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.readutf.gameservice.listeners.PreConnectListener;
import org.slf4j.Logger;

@Plugin(name = "Discovery", id = "discovery", version = "1.0.0", authors = "utf_")
public class DiscoveryProxy {

    private final ProxyServer proxy;
    private final Logger logger;
    private final GameServiceApi gameServiceApi;

    @Inject
    public DiscoveryProxy(ProxyServer proxy, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;
        this.gameServiceApi = new GameServiceApi(System.getenv("GAME_SERVICE_URL"));

        logger.info("DiscoveryProxy initialized with Game Service URL: {}", System.getenv("GAME_SERVICE_URL"));
    }
    
    @Subscribe
    public void onInit(ProxyInitializeEvent event) {
        logger.info("Initializing DiscoveryProxy...");

        proxy.getEventManager().register(this, new PreConnectListener(proxy, gameServiceApi));
    }
    
}
