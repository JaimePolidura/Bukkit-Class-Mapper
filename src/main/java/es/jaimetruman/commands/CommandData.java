package es.jaimetruman.commands;

import es.jaimetruman.commands.commandrunners.CommandRunner;
import es.jaimetruman.commands.commandrunners.CommandRunnerArgs;
import es.jaimetruman.commands.commandrunners.CommandRunnerNonArgs;
import lombok.Getter;

public final class CommandData {
    @Getter private final String command;
    @Getter private final boolean canBeTypedInConsole;
    @Getter private final String permissions;
    @Getter private final boolean isAsync;
    @Getter private final boolean isSubcommand;
    @Getter private final String[] args;
    @Getter private final CommandRunner runner;
    @Getter private final String usage;
    @Getter private final String helperCommand;
    @Getter private final String explanation;
    @Getter private final boolean isHelper;

    public CommandData(String command, boolean canBeTypedInConsole, String permissions, boolean isAsync,
                       String[] args, CommandRunner runner, String usage, String helperCommand, String explanation,
                       boolean isHelper) {
        this.command = command;
        this.canBeTypedInConsole = canBeTypedInConsole;
        this.permissions = permissions;
        this.isAsync = isAsync;
        this.runner = runner;
        this.usage = usage;
        this.helperCommand = helperCommand;
        this.explanation = explanation;
        this.isHelper = isHelper;
        this.isSubcommand = this.command.split(" ").length > 1;
        this.args = args;
    }


    public boolean isWithoutArgs(){
        return runner instanceof CommandRunnerNonArgs;
    }

    public boolean isWithArgs(){
        return runner instanceof CommandRunnerArgs;
    }

    public boolean canBeTypedInConsole(){
        return this.canBeTypedInConsole;
    }
}
