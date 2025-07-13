package org.readutf.gameservice.game;

import java.util.List;
import java.util.UUID;

public record GameRequest(
        String playlist,
        List<List<UUID>> teams
) {}
