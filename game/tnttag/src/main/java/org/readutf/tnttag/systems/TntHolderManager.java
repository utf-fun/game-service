package org.readutf.tnttag.systems;

import io.github.togar2.pvp.events.FinalAttackEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import kotlin.collections.ArrayDeque;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.LodestoneTracker;
import net.minestom.server.network.packet.server.play.ExplosionPacket;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.scoreboard.TeamBuilder;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.readutf.engine.GameException;
import org.readutf.engine.event.listener.ListenerData;
import org.readutf.engine.event.listener.TypedGameListener;
import org.readutf.engine.feature.System;
import org.readutf.engine.feature.spectator.SpectatorData;
import org.readutf.engine.feature.spectator.SpectatorSystem;
import org.readutf.engine.minestom.MinestomPlatform;
import org.readutf.engine.minestom.PlatformUtils;
import org.readutf.tnttag.stages.fighting.FightingStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TntHolderManager implements System {

    private static final Logger logger = LoggerFactory.getLogger(TntHolderManager.class);

    private @NotNull final SpectatorSystem spectatorSystem;
    private @NotNull final FightingStage fightingStage;
    private final List<UUID> tagged = new ArrayDeque<>();

    private final Team taggedTeam =
            new TeamBuilder("tagged", MinecraftServer.getTeamManager())
                    .prefix(Component.text("[IT] ", NamedTextColor.RED))
                    .teamColor(NamedTextColor.RED)
                    .nameTagVisibility(TeamsPacket.NameTagVisibility.ALWAYS)
                    .build();

    public TntHolderManager(@NotNull SpectatorSystem spectatorSystem, @NotNull FightingStage fightingStage) {
        this.spectatorSystem = spectatorSystem;
        this.fightingStage = fightingStage;
    }

    @Override
    public @NotNull List<ListenerData> getListeners() {
        return List.of(
                ListenerData.typed(FinalAttackEvent.class, new DamageListener())
        );
    }

    private void onAttack(@NotNull Player damaged, @NotNull Player damager) {
        logger.info("{} Attacking {}", damager, damaged);
        if (isTagged(damager.getUuid()) && !isTagged(damaged.getUuid())) {
            setAsTagged(damaged);
            setAsNormal(damager);
        }
    }

    public boolean isTagged(@NotNull UUID uuid) {
        return tagged.contains(uuid);
    }

    public void setAsTagged(@NotNull UUID uuid) {
        Player player = MinestomPlatform.getPlayer(uuid);
        if (player == null) return;
        setAsTagged(player);
    }

    public void setAsTagged(@NotNull Player player) {
        player.sendMessage("You are now tagged!");

        tagged.add(player.getUuid());
        player.setTeam(taggedTeam);
        player.set(DataComponents.CUSTOM_NAME, Component.text(String.format("[IT] %s", player.getName()), NamedTextColor.RED));

        player.getInventory().setItemStack(0, ItemStack.of(Material.TNT));
        player.getInventory().setEquipment(EquipmentSlot.HELMET, (byte) 0, ItemStack.of(Material.TNT));

        Pos position = player.getPosition();

        var compassItem = ItemStack.of(Material.COMPASS).with(
                DataComponents.LODESTONE_TRACKER,
                new LodestoneTracker(
                        "",
                        position,
                        true
                ));
        player.getInventory().setItemStack(4, compassItem);

        player.playSound(Sound.sound(SoundEvent.BLOCK_GRASS_BREAK, Sound.Source.PLAYER, 3f, 1f));

        Component message = Component.text(String.format("%s is It!", player.getUsername()), NamedTextColor.RED);

        fightingStage.getGame().messageAll(message);
    }

    public void setAsNormal(@NotNull UUID playerId) {
        Player player = MinestomPlatform.getPlayer(playerId);
        if (player == null) return;
        setAsNormal(player);
    }

    public void setAsNormal(@NotNull Player player) {
        tagged.remove(player.getUuid());
        player.setTeam(null);
        player.set(DataComponents.CUSTOM_NAME, player.getName());

        player.getInventory().setItemStack(4, ItemStack.AIR);

        @NotNull ItemStack[] items = player.getInventory().getItemStacks();
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item.material() == Material.TNT) {
                player.getInventory().setItemStack(i, ItemStack.of(Material.AIR));
            }
        }
    }

    public void explode() throws GameException {
        for (UUID id : new ArrayDeque<>(tagged)) {
            logger.info("Exploding {}", id);

            Player player = MinestomPlatform.getPlayer(id);
            if (player == null) continue;
            setAsNormal(player);

            spectatorSystem.setSpectator(SpectatorData.permanent(
                    player.getUuid(),
                    LocalDateTime.now(),
                    PlatformUtils.fromPoint(player.getPosition()))
            );

            for (UUID onlinePlayer : fightingStage.getGame().getOnlinePlayers()) {
                Player online = MinestomPlatform.getPlayer(onlinePlayer);
                if (online == null) continue;

//                online.playSound(Sound.sound(SoundEvent.ENTITY_GENERIC_EXPLODE, Sound.Source.PLAYER, 1f, 1f));

                online.sendPacket(new ExplosionPacket(
                        player.getPosition(),
                        Pos.ZERO,
                        Particle.EXPLOSION,
                        SoundEvent.ENTITY_GENERIC_EXPLODE
                ));
            }
        }

        if (spectatorSystem.getAlivePlayers().size() > 1) {
            fightingStage.endStage();
        }

    }

    public List<UUID> getTagged() {
        return tagged;
    }

    private class DamageListener implements TypedGameListener<FinalAttackEvent> {

        @Override
        public void onTypedEvent(@NotNull FinalAttackEvent event) {
            if (event.getEntity() instanceof Player damager && event.getTarget() instanceof Player target) {
                onAttack(target, damager);
            }
        }
    }

}
