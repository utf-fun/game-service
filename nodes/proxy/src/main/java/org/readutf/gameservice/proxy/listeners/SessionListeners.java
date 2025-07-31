package org.readutf.gameservice.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import org.readutf.social.SocialClient;

public class SessionListeners {

    private final SocialClient socialClient;

    public SessionListeners(SocialClient socialClient) {
        this.socialClient = socialClient;
    }

    @Subscribe
    public void onPlayerJoin(ServerPostConnectEvent event) {

        if (event.getPreviousServer() != null) return;

        socialClient.playerConnect(event.getPlayer().getUniqueId());
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        socialClient.playerDisconnect(event.getPlayer().getUniqueId());
    }

}
