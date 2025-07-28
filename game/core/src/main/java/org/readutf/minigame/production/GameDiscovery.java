package org.readutf.minigame.production;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.readutf.gameservice.client.GameServiceClient;
import org.readutf.gameservice.client.game.GameRequestHandler;
import org.readutf.gameservice.client.platform.ContainerResolver;
import org.readutf.gameservice.client.platform.DockerResolver;
import org.readutf.gameservice.client.platform.KubernetesResolver;
import org.readutf.minigame.MinigameServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GameDiscovery implements GameRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(GameDiscovery.class);

    @NotNull
    private final MinigameServer minigameServer;

    public GameDiscovery(@NotNull MinigameServer minigameServer) {
        this.minigameServer = minigameServer;
        GameServiceClient client = GameServiceClient.builder(getResolver())
                .setTags(getDiscoveryTags())
                .setPlaylists(List.of(minigameServer.getPlaylist()))
                .setCapacitySupplier(minigameServer::getCapacity)
                .setRequestHandler(this)
                .build();

        new Thread(() -> client.startBlocking(new InetSocketAddress(System.getenv("DISCOVERY_HOST"), 50052))).start();
    }

    private static @NotNull ContainerResolver getResolver() {
        @Nullable String resolver = System.getenv("DISCOVERY_RESOLVER");

        ContainerResolver containerResolver;
        if ("kubernetes".equalsIgnoreCase(resolver)) {
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
    public UUID requestGame(String playlist, List<List<UUID>> list) throws Exception {
        log.info("Requesting game for playlist: {} with teams: {}", playlist, list);

        if (!minigameServer.getPlaylist().equalsIgnoreCase(playlist)) return null;
        try {
            return minigameServer.start(list);
        } catch (Exception e) {
            log.error("Failed to start minigame server", e);
            throw e;
        }
    }
}
