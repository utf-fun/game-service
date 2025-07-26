package org.readutf.tnttag.stages.fighting;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;

import org.readutf.engine.minestom.system.actionbar.ActionBarProvider;
import org.readutf.tnttag.systems.TntHolderManager;

public class TagActionBar implements ActionBarProvider {

    private final TntHolderManager tntHolderManager;
    private long lastUpdate = 0L;

    public TagActionBar(TntHolderManager tntHolderManager) {
        this.tntHolderManager = tntHolderManager;
    }

    @Override
    public Component getText(Player player) {
        if (tntHolderManager.isTagged(player.getUuid())) {
            long sinceLastUpdate = System.currentTimeMillis() - lastUpdate;
            if(sinceLastUpdate > 1000) {
                lastUpdate = System.currentTimeMillis();
                return Component.text("Your IT, tag someone!").color(NamedTextColor.RED);
            } else if (sinceLastUpdate > 500) {
                return Component.text("Your IT, tag someone!").color(NamedTextColor.WHITE);
            } else {
                return Component.text("Your IT, tag someone!").color(NamedTextColor.RED);
            }
        } else {
            return Component.text("Run away!").color(NamedTextColor.GREEN);
        }
    }
}