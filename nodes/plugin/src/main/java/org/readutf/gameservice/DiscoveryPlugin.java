package org.readutf.gameservice;


import org.bukkit.plugin.java.JavaPlugin;
import org.readutf.gameservice.client.GameServiceClient;
import org.readutf.gameservice.client.ReconnectingGameService;
import org.readutf.gameservice.client.platform.DockerPlatform;

public class DiscoveryPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        ReconnectingGameService reconnecting = GameServiceClient.reconnecting("orchestrator:50052", new DockerPlatform(), () -> 0.5f);
    }
}