package es.jaimetruman.commands;

import org.bukkit.command.CommandSender;

/**
 * To create a command you should annotate your class with @Command and
 * implement this interface. When the command is typed the code in execute method
 * will be executed
 */
public interface CommandRunner {
    void execute(CommandSender sender, String[] args);
}
