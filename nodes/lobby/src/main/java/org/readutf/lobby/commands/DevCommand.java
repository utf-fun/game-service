package org.readutf.lobby.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class DevCommand extends Command {

    public DevCommand() {
        super("dev");

        setDefaultExecutor((sender, command) -> {
            if(sender instanceof Player player) {
                player.sendPluginMessage("custom:switch_server", "test");
            }
        });
    }
}
