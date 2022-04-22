package es.jaimetruman.commands;

import java.util.*;

public final class CommandRegistry {
    //Command name -> commandData
    private final Map<String, CommandData> commands;
    // Command name -> set os subcommands commandata
    private final Map<String, Set<CommandData>> subcommands;
    private final BukkitUsageMessageBuilder bukkitUsageMessageBuilder;

    public CommandRegistry(){
        this.commands = new HashMap<>();
        this.subcommands = new HashMap<>();
        this.bukkitUsageMessageBuilder = new BukkitUsageMessageBuilder();
    }

    public void put(CommandRunner commandRunnerInstance, Command commandInfo){
        String[] args = commandInfo.args();
        String command = commandInfo.value();
        CommandData commandData = new CommandData( command, commandInfo.canBeTypedInConsole(),
                commandInfo.permissions(), commandInfo.isAsync(), args, commandRunnerInstance,
                commandInfo.helperCommand(), this.bukkitUsageMessageBuilder.build(command, args),
                commandInfo.explanation()
        );

        this.commands.put(commandInfo.value(), commandData);

        if(commandData.isSubcommand())
            addToSubCommandList(commandData);
    }

    public Set<CommandData> findSubcommandsByCommandName(String mainCommand){
        return this.subcommands.get(mainCommand);
    }

    private void addToSubCommandList(CommandData commandData) {
        String mainCommand = commandData.getCommand().split(" ")[0];

        this.subcommands.putIfAbsent(mainCommand, new HashSet<>())
                .add(commandData);
    }

    public Optional<CommandData> findByName(String commandName, String[] args){
        CommandData commandData = this.commands.get(commandName);
        boolean commandFound = commandData != null;

        return commandFound ?
                Optional.of(commandData) :
                args.length > 0 ?
                        findSubCommand(commandName, args) :
                        Optional.empty();
    }

    private Optional<CommandData> findSubCommand(String commandName, String[] args){
        CommandData subCommandData = this.commands.get(String.format("%s %s", commandName, args[0]));

        return subCommandData != null && subCommandData.isSubcommand() ?
                Optional.of(subCommandData) :
                Optional.empty();
    }
}
