package org.readutf.tnttag.commands;

import java.util.Arrays;
import java.util.List;
import net.minestom.server.command.builder.Command;

public class CommandHelper {

    public static List<Command> commands = Arrays.asList(
            new AttributeCommand(),
            new AutoViewCommand(),
            new BookCommand(),
            new ConfigCommand(),
            new CookieCommand(),
            new DebugGridCommand(),
            new DimensionCommand(),
            new DisplayCommand(),
            new EchoCommand(),
            new EntitySelectorCommand(),
            new ExecuteCommand(),
            new FindCommand(),
            new GamemodeCommand(),
            new GiveCommand(),
            new HealthCommand(),
            new HorseCommand(),
            new KillCommand(),
            new NotificationCommand(),
            new PlayersCommand(),
            new PotionCommand(),
            new RedirectTestCommand(),
            new RelightCommand(),
            new RemoveCommand(),
            new SaveCommand(),
            new SetBlockCommand(),
            new SetEntityType(),
            new ShootCommand(),
            new ShutdownCommand(),
            new SidebarCommand(),
            new SummonCommand(),
            new TeleportCommand(),
            new TitleCommand(),
            new WeatherCommand(),
            new WorldBorderCommand()
    );

}
