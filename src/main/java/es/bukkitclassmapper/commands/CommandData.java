package es.bukkitclassmapper.commands;

import es.bukkitclassmapper.commands.commandrunners.CommandRunner;
import es.bukkitclassmapper.commands.commandrunners.CommandRunnerNonArgs;
import lombok.AllArgsConstructor;
import lombok.Getter;

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

    public boolean isSubcommand() {
        return this.command.split(" ").length > 1;
    }

    public boolean isMainCommand() {
        return this.command.split(" ").length == 0;
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
}
