package org.readutf.tnttag.stages.fighting.tasks;

import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.component.LodestoneTracker;
import org.jetbrains.annotations.NotNull;
import org.readutf.engine.minestom.MinestomPlatform;
import org.readutf.engine.task.impl.RepeatingGameTask;
import org.readutf.tnttag.stages.fighting.FightingStage;
import org.readutf.tnttag.systems.TntHolderManager;

public class CompassTask extends RepeatingGameTask {

    private final FightingStage stage;
    private @NotNull final TntHolderManager tntHolderManager;

    public CompassTask(FightingStage stage, @NotNull TntHolderManager tntHolderManager) {
        super(0, 1);
        this.stage = stage;
        this.tntHolderManager = tntHolderManager;
    }

    @Override
    public void run() {
        for (UUID playerId : tntHolderManager.getTagged()) {
            Player player = MinestomPlatform.getPlayer(playerId);
            if (player == null) continue;

            Player nearestPlayer = stage.getGame().getOnlinePlayers().stream()
                    .filter(id -> !tntHolderManager.isTagged(id))
                    .map(MinestomPlatform::getPlayer)
                    .filter(Objects::nonNull)
                    .min(Comparator.comparingDouble(p -> p.getPosition().distance(player.getPosition())))
                    .orElse(null);

            if (nearestPlayer == null) continue;

            ItemStack itemstack = player.getInventory().getItemStack(4);
            if (itemstack.has(DataComponents.LODESTONE_TRACKER)) {
                LodestoneTracker lodestone = itemstack.get(DataComponents.LODESTONE_TRACKER);
                if (lodestone != null) {
                    new LodestoneTracker(
                            "",
                            nearestPlayer.getPosition(),
                            true
                    );
                }
            }
        }
    }
}