package org.readutf.gameservice;


import org.bukkit.plugin.java.JavaPlugin;
import org.readutf.gameservice.client.GameServiceClient;
import org.readutf.gameservice.client.ReconnectingGameService;
import org.readutf.gameservice.client.platform.DockerPlatform;

public class DiscoveryPlugin extends JavaPlugin {

    private final ReconnectingGameService gameService;

    public DiscoveryPlugin() {
        this.gameService = GameServiceClient.reconnecting(System.getenv("DISCOVERY_HOST") + ":" + System.getenv("DISCOVERY_PORT"), new DockerPlatform(), () -> 0.5f);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        this.gameService.shutdown();
    }
}