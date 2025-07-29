package org.readutf.lobby.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevCommand extends Command {

    private static final Logger log = LoggerFactory.getLogger(DevCommand.class);

    public DevCommand() {
        super("dev");

        setDefaultExecutor((sender, command) -> {
            sender.sendMessage("test123");
            log.info("Dev command executed");
            if(sender instanceof Player player) {
                player.sendMessage("sending message");
                player.sendPluginMessage("custom:switch_server", "test");
            }
        });
    }
}
