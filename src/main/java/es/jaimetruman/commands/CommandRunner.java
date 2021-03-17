package es.jaimetruman.commands;

import org.bukkit.command.CommandSender;

public interface CommandRunner {
    void execute(CommandSender sender, String[] args);
}
