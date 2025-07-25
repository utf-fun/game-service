package org.readutf.tnttag.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentCommand;

public class ExecuteCommand extends Command {

    public ExecuteCommand() {
        super("execute");
        ArgumentCommand run = new ArgumentCommand("run");

        addSyntax(((sender, context) -> {}), run);
    }
}
