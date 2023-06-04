package es.bukkitclassmapper.commands.commandrunners;

import org.bukkit.entity.Player;

public interface CommandRunnerArgs<T> extends CommandRunner{
    void execute(T args, Player player);
}
