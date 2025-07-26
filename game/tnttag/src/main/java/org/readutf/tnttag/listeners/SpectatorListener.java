package org.readutf.tnttag.listeners;

import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.readutf.engine.event.listener.TypedGameListener;
import org.readutf.engine.feature.spectator.GameSpectateEvent;
import org.readutf.engine.minestom.MinestomPlatform;
import org.readutf.tnttag.TagGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpectatorListener implements TypedGameListener<GameSpectateEvent> {

    private static final Logger log = LoggerFactory.getLogger(SpectatorListener.class);
    private final @NotNull TagGame game;

    public SpectatorListener(@NotNull TagGame game) {
        this.game = game;
    }

    @Override
    public void onTypedEvent(@NotNull GameSpectateEvent event) {

        UUID playerId = event.getSpectatorData().getPlayerId();
        Player player = MinestomPlatform.getPlayer(playerId);
        if (player == null) {
            log.info("Spectate event occurred for offline player");
            return;
        }

        log.info("Player {} is now spectating {}", event.getSpectatorData().getPlayerId(), event.getSpectatorData());

        if (game.getSpectatorSystem().getAlivePlayers().size() == 1) {
            game.messageAll(Component.text("finished. TODO"));
        }

    }
}
