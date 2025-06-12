package org.readutf.gameservice;


import org.bukkit.plugin.java.JavaPlugin;
import org.readutf.gameservice.client.GameServiceClient;
import org.readutf.gameservice.client.ReconnectingGameService;
import org.readutf.gameservice.client.platform.DockerPlatform;

public class DiscoveryPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        String host = System.getenv("DISCOVERY_HOST") + ":" + System.getenv("DISCOVERY_PORT");

        ReconnectingGameService reconnecting = GameServiceClient.reconnecting(":50052", new DockerPlatform(), () -> 0.5f);
    }
}