package es.jaimetruman.commands.commandrunners;

import org.bukkit.command.CommandSender;

public interface CommandRunnerNonArgs extends CommandRunner {
    void execute(CommandSender sender);
}
