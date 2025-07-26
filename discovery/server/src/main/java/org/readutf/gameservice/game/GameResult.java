package org.readutf.gameservice.game;

import org.readutf.gameservice.common.Server;

import java.util.UUID;

public record GameResult(UUID gameId, Server server) {
}
