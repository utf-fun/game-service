package org.readutf.tnttag.spawning;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import org.readutf.buildformat.common.markers.Position;
import org.readutf.engine.feature.spawning.SpawnFinder;
import org.readutf.tnttag.positions.TagPositions;

public record TagSpawning(TagPositions tagPositions) implements SpawnFinder {

    @Override
    public @Nullable Position find(UUID playerId) {
        return tagPositions.positions().getFirst();
    }
}
