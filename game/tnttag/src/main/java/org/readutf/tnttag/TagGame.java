package org.readutf.tnttag;

import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.readutf.engine.Game;
import org.readutf.engine.GamePlatform;
import org.readutf.engine.arena.Arena;
import org.readutf.engine.event.GameEventManager;
import org.readutf.engine.event.exceptions.EventDispatchException;
import org.readutf.engine.feature.spectator.SpectatorSystem;
import org.readutf.engine.feature.visibility.VisibilitySystem;
import org.readutf.engine.minestom.system.spectator.MinestomSpectator;
import org.readutf.engine.minestom.system.visibility.MinestomVisibilityPlatform;
import org.readutf.engine.task.GameScheduler;
import org.readutf.engine.team.GameTeam;
import org.readutf.engine.team.TeamSelector;
import org.readutf.tnttag.positions.TagPositions;
import org.readutf.tnttag.stages.WarmupStage;
import org.readutf.tnttag.stages.fighting.FightingStage;

public class TagGame extends Game<Instance, Arena<Instance, TagPositions>, GameTeam> {

    private @NotNull final SpectatorSystem spectatorSystem;

    /**
     * Creates a new game instance with the specified components
     *
     * @param platform
     * @param scheduler    Game scheduler for timing tasks
     * @param eventManager Event system for the game
     * @param teamSelector Team assignment strategy
     */
    public TagGame(@NotNull GamePlatform<Instance> platform, @NotNull GameScheduler scheduler, @NotNull GameEventManager eventManager, @NotNull TeamSelector<GameTeam> teamSelector) throws EventDispatchException {
        super(platform, scheduler, eventManager, teamSelector);

        registerStage(WarmupStage::new);
        registerStage((game1, previousStage) -> new FightingStage(this, previousStage, 1));

        this.spectatorSystem = addSystem(new SpectatorSystem(this, new MinestomSpectator(), new VisibilitySystem(this, new MinestomVisibilityPlatform())));
    }

    public @NotNull SpectatorSystem getSpectatorSystem() {
        return spectatorSystem;
    }
}
