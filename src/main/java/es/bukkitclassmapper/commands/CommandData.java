package es.bukkitclassmapper.commands;

import es.bukkitclassmapper.commands.commandrunners.CommandRunner;
import es.bukkitclassmapper.commands.commandrunners.CommandRunnerArgs;
import es.bukkitclassmapper.commands.commandrunners.CommandRunnerNonArgs;
import lombok.Getter;

public final class CommandData {
    @Getter private final String command;
    @Getter private final String permissions;
    @Getter private final boolean isSubcommand;
    @Getter private final String[] args;
    @Getter private final CommandRunner runner;
    @Getter private final String usage;
    @Getter private final String helperCommand;
    @Getter private final String explanation;
    @Getter private final boolean isHelper;
    @Getter private final boolean isIO;

    public CommandData(String command, String permissions, String[] args, CommandRunner runner,
                       String usage, String helperCommand, String explanation, boolean isHelper, boolean isIO) {
        this.command = command;
        this.permissions = permissions;
        this.runner = runner;
        this.usage = usage;
        this.helperCommand = helperCommand;
        this.explanation = explanation;
        this.isHelper = isHelper;
        this.isSubcommand = this.command.split(" ").length > 1;
        this.args = args;
        this.isIO = isIO;
    }

    public CommandData(String command, String permissions, String[] args, CommandRunner runner,
                       String usage, String helperCommand, String explanation, boolean isHelper, boolean isSubcommand, boolean isIO) {
        this.command = command;
        this.permissions = permissions;
        this.runner = runner;
        this.usage = usage;
        this.helperCommand = helperCommand;
        this.explanation = explanation;
        this.isHelper = isHelper;
        this.isSubcommand = isSubcommand;
        this.args = args;
        this.isIO = isIO;
    }

    public boolean isWithoutArgs(){
        return runner instanceof CommandRunnerNonArgs;
    }

    public boolean isWithArgs(){
        return runner instanceof CommandRunnerArgs;
    }
}
