package es.jaimetruman.commands;

import org.bukkit.command.CommandSender;

public interface CommandRunnerArgs<T> extends CommandRunner{
    void execute(T args, CommandSender sender);
}
