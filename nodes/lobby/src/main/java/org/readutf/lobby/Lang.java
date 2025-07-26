package org.readutf.lobby;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Lang {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static Component SHUTDOWN_WARNING = miniMessage.deserialize("<yellow><bold>⚠</bold></yellow> <white>Server will shutdown in 5 minutes</white>");

}
