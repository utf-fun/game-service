package org.readutf.tnttag.stages.fighting;

import java.util.UUID;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.readutf.engine.feature.scoreboard.Scoreboard;
import org.readutf.engine.feature.spectator.SpectatorSystem;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.readutf.tnttag.systems.TntHolderManager;

public class FightingScoreboard implements Scoreboard {

    private @NotNull final SpectatorSystem spectatorFeature;
    private final int roundNumber;
    private @NotNull final String arenaName;
    private @NotNull final TntHolderManager tntHolderManager;
    private @NotNull final Supplier<LocalDateTime> explosionTimeSupplier;

    public FightingScoreboard(
            @NotNull SpectatorSystem spectatorSystem,
            int roundNumber,
            @NotNull String arenaName,
            @NotNull TntHolderManager tntHolderManager,
            @NotNull Supplier<LocalDateTime> explosionTimeSupplier
    ) {
        this.spectatorFeature = spectatorSystem;
        this.roundNumber = roundNumber;
        this.arenaName = arenaName;
        this.tntHolderManager = tntHolderManager;
        this.explosionTimeSupplier = explosionTimeSupplier;
    }

    @Override
    public Component getTitle(UUID playerId) {
        return Component.text("Tnt Tag").color(NamedTextColor.BLUE);
    }

    @Override
    public List<Component> getLines(UUID playerId) {
        LocalDateTime explosionTime = explosionTimeSupplier.get();

        List<Component> lines = new ArrayList<>();
        lines.add(Component.empty());
        lines.add(Component.text("Round: " + roundNumber).color(NamedTextColor.WHITE));
        lines.add(Component.text("Map: " + arenaName).color(NamedTextColor.WHITE));
        lines.add(Component.empty());
        if (explosionTime != null) {
            long explodesIn = Duration.between(LocalDateTime.now(), explosionTime).toMillis();
            if (explodesIn > 0) {
                lines.add(Component.text("Explodes: ").color(NamedTextColor.WHITE).append(Component.text(formatDuration(explodesIn)).color(NamedTextColor.RED)));
            } else {
                lines.add(Component.text("Explodes: ").color(NamedTextColor.WHITE).append(Component.text("Now!").color(NamedTextColor.RED)));
            }
        }

        lines.add(Component.text("Goal: ").append(getGoal(playerId)));
        lines.add(Component.text("Players: " + spectatorFeature.getAlivePlayers().size()).color(NamedTextColor.WHITE));
        lines.add(Component.empty());
        lines.add(Component.text("fun.utf.lol").color(NamedTextColor.WHITE));
        return lines;
    }

    private Component getGoal(UUID playerId) {
        if (tntHolderManager.isTagged(playerId)) return Component.text("Tag someone!").color(NamedTextColor.RED);
        return Component.text("Run away!").color(NamedTextColor.GREEN);
    }

    public static String formatDuration(long milliseconds) {
        if (milliseconds < 5000) {
            double seconds = milliseconds / 1000.0;
            return String.format("%.1fs", seconds);
        } else {
            int totalSeconds = (int) (milliseconds / 1000);
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;

            if (minutes > 0 && seconds > 0) {
                return String.format("%dm%ds", minutes, seconds);
            } else if (minutes > 0) {
                return String.format("%dm", minutes);
            } else {
                return String.format("%ds", seconds);
            }
        }
    }
}