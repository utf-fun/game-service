package org.readutf.gameservice;

import org.bukkit.plugin.java.JavaPlugin;
import org.readutf.gameservice.client.GameServiceClient;
import org.readutf.gameservice.client.ReconnectingGameService;
import org.readutf.gameservice.client.platform.ContainerResolver;
import org.readutf.gameservice.client.platform.DockerResolver;
import org.readutf.gameservice.client.platform.KubernetesResolver;

import java.util.Arrays;
import java.util.List;

public class DiscoveryPlugin extends JavaPlugin {

    private final ReconnectingGameService gameService;

    public DiscoveryPlugin() {

        String resolver = System.getenv("DISCOVERY_RESOLVER");

        ContainerResolver containerResolver;
        if (resolver.equalsIgnoreCase("kubernetes")) {
            containerResolver = new KubernetesResolver();
        } else {
            containerResolver = new DockerResolver();
        }

        String tagsEnv = System.getenv("DISCOVERY_TAGS");
        tagsEnv = tagsEnv == null ? "" : tagsEnv;
        List<String> tags = Arrays.stream(tagsEnv.split("[,\r\n]+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        this.gameService = GameServiceClient.builder()
                .setHost(System.getenv("GAME_SERVICE_HOST"))
                .setPort(Integer.parseInt(System.getenv("GAME_SERVICE_PORT")))
                .setContainerResolver(containerResolver)
                .setTags(tags)
                .build();
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {
        this.gameService.shutdown();
    }
}