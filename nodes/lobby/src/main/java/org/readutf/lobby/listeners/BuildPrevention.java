package org.readutf.lobby.listeners;

import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;

import java.util.function.Consumer;

public class BuildPrevention {

    public static Consumer<PlayerBlockBreakEvent> breakPrevention() {
        return event -> {
            event.setCancelled(true);
        };
    }

    public static Consumer<PlayerBlockPlaceEvent> placePrevention() {
        return event -> {
            event.setCancelled(true);
        };
    }

}
