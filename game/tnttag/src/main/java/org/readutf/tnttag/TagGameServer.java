package org.readutf.tnttag;

import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.readutf.buildformat.common.exception.BuildFormatException;
import org.readutf.engine.GameException;
import org.readutf.engine.arena.Arena;
import org.readutf.engine.arena.ArenaManager;
import org.readutf.engine.arena.exception.ArenaLoadException;
import org.readutf.engine.event.GameEventManager;
import org.readutf.engine.minestom.MinestomPlatform;
import org.readutf.engine.minestom.event.MinestomEventPlatform;
import org.readutf.engine.minestom.schedular.MinestomSchedular;
import org.readutf.engine.task.GameScheduler;
import org.readutf.engine.team.GameTeam;
import org.readutf.minigame.MinigameServer;
import org.readutf.minigame.arena.DefaultArenaManager;
import org.readutf.tnttag.positions.TagPositions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.IntStream;

public class TagGameServer extends MinigameServer {

    private static final Logger log = LoggerFactory.getLogger(TagGameServer.class);

    @NotNull
    private final MinestomPlatform platform;

    @NotNull
    private final GameScheduler scheduler;

    @NotNull
    private final GameEventManager eventManager;

    private final ArenaManager<Instance> arenaManager = DefaultArenaManager.createArenaManager();

    public TagGameServer(boolean production) {
        super("tnttag", production);
        this.platform = new MinestomPlatform();
        this.scheduler = new GameScheduler(new MinestomSchedular());
        this.eventManager = new GameEventManager(new MinestomEventPlatform());
    }

    @Override
    public UUID start(List<List<UUID>> teams) throws GameException {

        List<GameTeam> gameTeams = IntStream.range(0, teams.size())
                .mapToObj(i -> new GameTeam(String.valueOf(i)))
                .toList();

        TagGame game = new TagGame(platform, scheduler, eventManager, playerId -> {
            List<UUID> team = teams.stream()
                    .filter(uuids -> uuids.contains(playerId))
                    .findFirst()
                    .orElseThrow();
            return gameTeams.get(teams.indexOf(team));
        });

        List<String> arenaNames;
        try {
            arenaNames = new ArrayList<>(arenaManager.getByFormat("tnttag", TagPositions.class));
        } catch (BuildFormatException e) {
            throw new GameException("Failed to find arenas for tnttag", e);
        }

        if (arenaNames.isEmpty()) throw new GameException("Failed to load arena for tnttag");

        // shuffle the arenas to avoid players getting the same arena
        Collections.shuffle(arenaNames);

        for (String name : arenaNames) {
            try {
                Arena<Instance, TagPositions> arena = arenaManager.loadArena(name, TagPositions.class);
                if(arena == null) {
                    log.warn("Arena {} is null, skipping", name);
                    continue;
                }

                log.info("Loaded arena {}", name);

                game.changeArena(arena);

                game.start();

                log.info("Started game {} with arena {}", game.getId(), name);

                return game.getId();
            } catch (ArenaLoadException e) {
                e.printStackTrace();
                log.warn("Failed to load arena {}", name, e);
            }
        }

        throw new GameException("Failed to load any arena for tnttag");
    }

    @Override
    public void cancel(UUID gameId) {}

    @Override
    public float getCapacity() {
        return 0;
    }

    public static void main(String[] args) {
        new TagGameServer(true);
    }
}
