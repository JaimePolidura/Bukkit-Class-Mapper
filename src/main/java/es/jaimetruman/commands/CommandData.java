package es.jaimetruman.commands;

import lombok.Getter;

public final class CommandData {
    @Getter private final String command;
    private final boolean canBeTypedInConsole;
    @Getter private final String permissions;
    @Getter private final boolean isAsync;
    @Getter private final boolean isSubcommand;
    @Getter private final String[] args;
    @Getter private final CommandRunner runner;
    @Getter private final String usage;
    @Getter private final String helperCommand;

    public CommandData(String command, boolean canBeTypedInConsole, String permissions, boolean isAsync,
                       String[] args, CommandRunner runner, String usage, String helperCommand) {
        this.command = command;
        this.canBeTypedInConsole = canBeTypedInConsole;
        this.permissions = permissions;
        this.isAsync = isAsync;
        this.runner = runner;
        this.usage = usage;
        this.helperCommand = helperCommand;
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
