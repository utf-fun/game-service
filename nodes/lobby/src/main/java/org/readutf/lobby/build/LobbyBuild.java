package org.readutf.lobby.build;

import net.minestom.server.instance.Instance;
import org.readutf.buildformat.common.format.BuildFormat;
import org.readutf.buildformat.common.format.requirements.Requirement;
import org.readutf.buildformat.common.markers.Marker;
import org.readutf.buildformat.common.markers.Position;


public record LobbyBuild(
        Instance instance,
        LobbyPositions positions
) {
}