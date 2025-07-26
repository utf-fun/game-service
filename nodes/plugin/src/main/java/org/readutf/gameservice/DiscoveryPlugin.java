package org.readutf.gameservice;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.client.GameServiceClient;
import org.readutf.gameservice.client.platform.ContainerResolver;
import org.readutf.gameservice.client.platform.DockerResolver;
import org.readutf.gameservice.client.platform.KubernetesResolver;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

public class DiscoveryPlugin extends JavaPlugin {

//    private @NotNull final GameServiceClient serviceClient;
    private @NotNull final Thread clientThread;

    public DiscoveryPlugin() {

        this.clientThread = new Thread(() -> {
        });
        this.clientThread.setName("GameServiceClientThread");
        this.clientThread.setDaemon(false);
        this.clientThread.start();
    }

    private static @NotNull ContainerResolver getResolver() {
        String resolver = System.getenv("DISCOVERY_RESOLVER");

        ContainerResolver containerResolver;
        if (resolver.equalsIgnoreCase("kubernetes")) {
            containerResolver = new KubernetesResolver();
        } else {
            containerResolver = new DockerResolver();
        }
        return containerResolver;
    }

    private static @NotNull List<String> getDiscoveryTags() {
        String tagsEnv = System.getenv("DISCOVERY_TAGS");
        tagsEnv = tagsEnv == null ? "" : tagsEnv;
        return Arrays.stream(tagsEnv.split("[,\r\n]+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
//        serviceClient.stop();
        clientThread.interrupt();
    }
}