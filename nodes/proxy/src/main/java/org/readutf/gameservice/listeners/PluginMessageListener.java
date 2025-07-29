package org.readutf.gameservice.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.readutf.gameservice.DiscoveryProxy;
import org.readutf.gameservice.GameServiceApi;
import org.readutf.gameservice.common.Server;
import org.readutf.gameservice.exceptions.GameServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class PluginMessageListener {

    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("custom:switch_server");
    private static final Logger log = LoggerFactory.getLogger(PluginMessageListener.class);

    private final @NotNull DiscoveryProxy proxy;
    private final @NotNull GameServiceApi gameServiceApi;

    public PluginMessageListener(@NotNull DiscoveryProxy proxy, @NotNull GameServiceApi gameServiceApi) {
        this.proxy = proxy;
        this.gameServiceApi = gameServiceApi;
    }

    @Subscribe
    public void onMessage(PluginMessageEvent event) {
        if (!(event.getSource() instanceof Player player)) {
            log.warn("Received switch server message from non-player source: {}", event.getSource());
            return;
        }

        System.out.println(event.getIdentifier().toString());

        if (event.getIdentifier() == IDENTIFIER) {
            changeServer(event, player);
        }
    }

    private void changeServer(PluginMessageEvent event, Player player) {

        System.out.println(new String(event.getData()));

        //        UUID serverId = UUID.fromString(new String(event.getData()));
        //
        //        @Nullable RegisteredServer proxyServer = proxy.getServer(serverId);
        //        if (proxyServer != null) {
        //            player.createConnectionRequest(proxyServer).fireAndForget();
        //        }
        //
        //        try {
        //            Server server = gameServiceApi.getServer(serverId);
        //            RegisteredServer newServer = proxy.getServer(server);
        //
        //            player.createConnectionRequest(newServer).fireAndForget();
        //        } catch (GameServiceException e) {
        //            log.error("Failed to fetch server {} from Game Service: {}", serverId, e.getMessage());
        //        }
    }
}
