package org.readutf.tnttag.stages;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.minestom.server.instance.Instance;
import org.jspecify.annotations.NonNull;
import org.readutf.engine.Game;
import org.readutf.engine.arena.Arena;
import org.readutf.engine.feature.spawning.SpawningSystem;
import org.readutf.engine.stage.Stage;
import org.readutf.engine.stage.StageCreator;
import org.readutf.engine.task.impl.RepeatingGameTask;
import org.readutf.engine.team.GameTeam;
import org.readutf.tnttag.positions.TagPositions;
import org.readutf.tnttag.spawning.TagSpawning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Warmup stage for TNT Tag.
 * Waits for enough players to join, then starts the game after a countdown.
 */
public class WarmupStage extends Stage<Instance, Arena<Instance, TagPositions>, GameTeam> {

    private static StageCreator<Instance, Arena<Instance, TagPositions>, GameTeam> defaultCreator =
            (game1, previousStage1) -> new WarmupStage(
                    game1, previousStage1, 2, 60_000, 0, new long[] {5_000L, 4_000L, 3_000L, 2_000L, 1_000L});

    private static final Logger logger = LoggerFactory.getLogger(WarmupStage.class);

    // Minimum players required to start the game
    public final int targetPlayers;
    // Warmup countdown time in milliseconds (0 = instant)
    public final long warmupTime;
    // Intervals (in ms) at which to notify players of the countdown
    public final long[] warmupNotifyIntervals;

    // Time when the correct player count was first reached
    private long correctPlayersTime = 0L;

    // Maximum amount of time to wait before cancelling the game.
    private long maxWait;

    /**
     * Constructs a new stage associated with the given game and an optional previous stage.
     *
     * @param game          the game instance this stage belongs to
     * @param previousStage the previous stage, or null if this is the first stage
     */
    public WarmupStage(
            Game<Instance, Arena<Instance, TagPositions>, GameTeam> game,
            Stage<Instance, Arena<Instance, TagPositions>, GameTeam> previousStage,
            int targetPlayers,
            int maxWait,
            long warmupTime,
            long[] warmupNotifyIntervals) {
        super(game, previousStage);
        this.targetPlayers = targetPlayers;
        this.warmupTime = warmupTime;
        this.warmupNotifyIntervals = warmupNotifyIntervals;
        this.maxWait = maxWait;
        addSystem(new SpawningSystem.StageStart(
                game, new TagSpawning(getGame().getArena().getFormat())));
    }

    @Override
    protected void onStart() {
        schedule(new AwaitingPlayersTask());
    }

    /**
     * Repeating task that manages player waiting and countdown notifications.
     */
    private class AwaitingPlayersTask extends RepeatingGameTask {
        private final List<Long> intervals = new ArrayList<>();
        private LocalDateTime lastWaitingNotify = LocalDateTime.now().minusDays(1);

        public AwaitingPlayersTask() {
            super(0, 1);
            for (long interval : warmupNotifyIntervals) {
                intervals.add(interval);
            }
            // Sort in descending order for countdown
            intervals.sort(Collections.reverseOrder());
        }

        @Override
        public void run() {

            int currentPlayers = game.getPlayers().size();

            if (System.currentTimeMillis() - startTime > maxWait) {
                game.messageAll(Component.text("Game cancelled due to timeout, not enough players joined."));
                try {
                    game.end();
                } catch (org.readutf.engine.GameException e) {
                    logger.error(e.getMessage(), e);
                }
                return;
            }

            // Not enough players
            if (correctPlayersTime != 0L && currentPlayers < targetPlayers) {
                correctPlayersTime = 0L; // Reset
                game.messageAll(Component.text("Not enough players to start the game, waiting for more..."));
                intervals.clear();
                for (long interval : warmupNotifyIntervals) {
                    intervals.add(interval);
                }
                intervals.sort(Collections.reverseOrder());
                return;
            }

            // Just reached required player count
            if (correctPlayersTime == 0L && currentPlayers >= targetPlayers) {
                correctPlayersTime = System.currentTimeMillis();
                game.messageAll(Component.text("Game will start in " + formatMillis(warmupTime) + " seconds!"));
                return;
            }

            // Notify periodically if still waiting
            if (Duration.between(lastWaitingNotify, LocalDateTime.now()).abs().compareTo(Duration.ofSeconds(30)) > 0) {
                lastWaitingNotify = LocalDateTime.now();
                game.messageAll(Component.text("Waiting for more players to start the game..."));
            }

            // Countdown notifications
            List<Long> toRemove = new ArrayList<>();
            for (Long interval : intervals) {
                if (interval <= period) {
                    game.messageAll(Component.text("Game will start in " + formatMillis(interval) + " seconds!"));
                    toRemove.add(interval);
                }
            }
            intervals.removeAll(toRemove);

            // Countdown finished, start game
            if (correctPlayersTime != 0L && System.currentTimeMillis() - correctPlayersTime >= warmupTime) {
                game.messageAll(Component.text("Game started!"));
                try {
                    endStage();
                } catch (org.readutf.engine.GameException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        @Override
        public String toString() {
            return "AwaitingPlayersTask{" +
                    "intervals=" + intervals +
                    ", lastWaitingNotify=" + lastWaitingNotify +
                    ", delay=" + delay +
                    ", period=" + period +
                    ", lastTick=" + lastTick +
                    ", startTime=" + startTime +
                    ", markedForRemoval=" + isMarkedForRemoval() +
                    '}';
        }
    }

    /**
     * Formats milliseconds as HH:mm:ss.
     *
     * @param milliseconds Time in milliseconds
     * @return Formatted time string
     */
    @NonNull
    public String formatMillis(long milliseconds) {
        long hours = milliseconds / 3600000;
        long minutes = (milliseconds % 3600000) / 60000;
        long seconds = (milliseconds % 60000) / 1000;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}