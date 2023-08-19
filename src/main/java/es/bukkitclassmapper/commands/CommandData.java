package es.bukkitclassmapper.commands;

import es.bukkitclassmapper.commands.commandrunners.CommandRunner;
import es.bukkitclassmapper.commands.commandrunners.CommandRunnerNonArgs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
public final class CommandData {
    @Getter private final CommandRunner runner;
    @Getter private final String permissions;
    @Getter private final String explanation;
    @Getter private final String command;
    @Getter private final boolean isHelper;
    @Getter private final String[] args;
    @Getter private final String usage;
    @Getter private final boolean isAsync;
    @Getter private final boolean needsOp;

    public boolean isSubcommand() {
        return this.command.split(" ").length > 1;
    }

    public boolean isMainCommand() {
        return this.command.split(" ").length == 1;
    }

    public boolean isMainCommandHelper() {
        return this.isMainCommand() && this.isHelper;
    }

    public boolean isSubCommandHelper() {
        return this.isSubcommand() && this.isHelper;
    }

    public boolean isWithoutArgs(){
        return runner instanceof CommandRunnerNonArgs;
    }

    public String getMainCommand() {
        return command.split(" ")[0];
    }

    public String getSubCommand() {
        return command.split(" ")[1];
    }

    public boolean canExecute(CommandSender sender) {
        if(this.needsOp && !sender.isOp()) {
            return false;
        }
        if(!this.permissions.equals("") && !sender.hasPermission(this.permissions)){
            return false;
        }

        return true;
    }
}
