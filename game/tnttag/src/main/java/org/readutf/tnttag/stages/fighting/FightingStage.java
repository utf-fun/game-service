package org.readutf.tnttag.stages.fighting;

import io.github.togar2.pvp.events.PlayerExhaustEvent;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.readutf.engine.arena.Arena;
import org.readutf.engine.event.exceptions.EventDispatchException;
import org.readutf.engine.event.listener.TypedGameListener;
import org.readutf.engine.feature.scoreboard.ScoreboardSystem;
import org.readutf.engine.feature.spectator.GameSpectateEvent;
import org.readutf.engine.minestom.MinestomPlatform;
import org.readutf.engine.minestom.system.actionbar.ActionBarSystem;
import org.readutf.engine.minestom.system.scoreboard.MinestomScoreboard;
import org.readutf.engine.stage.Stage;
import org.readutf.engine.stage.exception.StageChangeException;
import org.readutf.engine.team.GameTeam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.readutf.tnttag.TagGame;
import org.readutf.tnttag.listeners.SpectatorListener;
import org.readutf.tnttag.positions.TagPositions;
import org.readutf.tnttag.stages.fighting.tasks.CompassTask;
import org.readutf.tnttag.stages.fighting.tasks.ExplosionTask;
import org.readutf.tnttag.stages.fighting.tasks.FightingWarmupTask;
import org.readutf.tnttag.systems.TntHolderManager;
import org.readutf.tnttag.systems.combat.DisableEventSystem;
import org.readutf.tnttag.systems.combat.ZeroDamageSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TntTagFightingStage represents the main fighting phase of the TNT Tag game.
 * During this stage, players are actively playing, one player is "it" (holding TNT),
 * and there's a countdown until the TNT explodes.
 */
public class FightingStage extends Stage<Instance, Arena<Instance, TagPositions>, GameTeam> {

    /**
     * Duration until the TNT explodes during the fighting stage.
     */
    public @NotNull static final Duration EXPLOSION_DURATION = Duration.ofSeconds(20);
    private static final Logger log = LoggerFactory.getLogger(FightingStage.class);
    private final int roundNumber;
    private @NotNull final TntHolderManager tntHolderManager;
    private @Nullable LocalDateTime explosionTime;
    private final ScoreboardSystem scoreboardSystem;
    private final ActionBarSystem actionBarSystem;
    private final TagGame tagGame;

    /**
     * Creates a new TntTagFightingStage.
     *
     * @param game          The TntTagGame instance
     * @param previousStage The previous stage, or null if this is the first stage
     * @param roundNumber   The current round number
     */
    public FightingStage(TagGame game, Stage<Instance, Arena<Instance, TagPositions>, GameTeam> previousStage, int roundNumber) {
        super(game, previousStage);
        this.tagGame = game;
        this.roundNumber = roundNumber;
        Arena<Instance, TagPositions> arena = game.getArena();
        if (arena == null) throw new StageChangeException("Arena must be set before creating a FightingStage.");

        // Initialize TNT holder manager to track who has the TNT
        this.tntHolderManager = new TntHolderManager(game.getSpectatorSystem(), this);
        this.actionBarSystem = new ActionBarSystem(game, new TagActionBar(tntHolderManager));
        this.scoreboardSystem = new ScoreboardSystem(new MinestomScoreboard());

        // Calculate when the TNT will explode
        this.explosionTime = null;

        for (UUID player : getGame().getPlayers()) {
            scoreboardSystem.setScoreboard(player, new FightingScoreboard(game.getSpectatorSystem(), roundNumber, arena.getBuildMeta().name(), tntHolderManager, () -> explosionTime));
        }
    }

    @Override
    public void onStart() throws EventDispatchException {
        this.schedule(new FightingWarmupTask(this));
        this.addSystem(scoreboardSystem);
        this.addSystem(actionBarSystem);
        this.addSystem(tntHolderManager);
        this.addSystem(new DisableEventSystem(
                PlayerBlockBreakEvent.class, PlayerBlockPlaceEvent.class, PlayerBlockInteractEvent.class,
                PlayerExhaustEvent.class
        ));
        this.addSystem(new ZeroDamageSystem());
        this.registerListener(new SpectatorListener(tagGame), GameSpectateEvent.class);
        this.registerListener(spectatorListener, GameSpectateEvent.class);
    }

    /**
     * Initializes the game state by setting a random player as "it" and
     * setting up scoreboards for all players.
     *
     */
    public void selectChasers() {
        log.info("Selecting chasers for round {}", roundNumber);

        // Get all online players and randomly select one to be "it"
        List<UUID> onlinePlayers = game.getOnlinePlayers();
        if (!onlinePlayers.isEmpty()) {
            // Select a random player to start with the TNT
            UUID randomPlayer = onlinePlayers.get((int) (Math.random() * onlinePlayers.size()));
            tntHolderManager.setAsTagged(randomPlayer);
        }

        this.explosionTime = LocalDateTime.now().plus(EXPLOSION_DURATION);
        schedule(new ExplosionTask(tntHolderManager));
        schedule(new CompassTask(this, tntHolderManager));
    }

    private final TypedGameListener<GameSpectateEvent> spectatorListener = event -> {
        UUID playerId = event.getSpectatorData().getPlayerId();
        Player player = MinestomPlatform.getPlayer(playerId);
        if (player == null) return;

        player.getInventory().clear();
    };

}