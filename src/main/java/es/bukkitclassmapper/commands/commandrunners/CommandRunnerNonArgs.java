package es.bukkitclassmapper.commands.commandrunners;

import org.bukkit.entity.Player;

public interface CommandRunnerNonArgs extends CommandRunner {
    void execute(Player player);
}
