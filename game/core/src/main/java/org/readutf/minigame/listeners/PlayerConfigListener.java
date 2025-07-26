package org.readutf.minigame.listeners;

import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.Instance;
import org.readutf.engine.Game;
import org.readutf.engine.GameManager;
import org.readutf.engine.arena.Arena;

import java.util.function.Consumer;

public class PlayerConfigListener implements Consumer<AsyncPlayerConfigurationEvent> {
    @Override
    public void accept(AsyncPlayerConfigurationEvent event) {

        Game<?, ? extends Arena<Instance, ?>, ?> game = (Game<?, ? extends Arena<Instance, ?>, ?>) GameManager.getGameByPlayer(event.getPlayer().getUuid());
        if(game == null) {
            event.getPlayer().kick("Error finding your game.");
            return;
        }

        Arena<Instance, ?> arena = game.getArena();
        if(arena == null) {
            event.getPlayer().kick("The game has not been started yet!");
        }

        event.setSpawningInstance(arena.getWorld());
    }
}
