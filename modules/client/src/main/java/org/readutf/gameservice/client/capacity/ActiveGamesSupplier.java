package org.readutf.gameservice.client.capacity;

import org.readutf.gameservice.common.Game;

import java.util.List;

@FunctionalInterface
public interface ActiveGamesSupplier {

    List<Game> getActiveGames();

}
