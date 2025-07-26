package org.readutf.gameservice.client.game;

import java.util.List;
import java.util.UUID;

@FunctionalInterface
public interface GameRequestHandler {

    UUID requestGame(String playlist, List<List<UUID>> teams) throws Exception;

}
