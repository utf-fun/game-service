package org.readutf.lobby.build;

import org.readutf.buildformat.common.format.BuildFormat;
import org.readutf.buildformat.common.format.requirements.Requirement;
import org.readutf.buildformat.common.markers.Marker;
import org.readutf.buildformat.common.markers.Position;

public record LobbyPositions(
        @Requirement(name = "spawn") Marker spawn
) implements BuildFormat {
}
