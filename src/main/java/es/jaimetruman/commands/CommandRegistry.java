package es.jaimetruman.commands;

import java.util.*;

public final class CommandRegistry {
    //Command name -> commandData
    private final Map<String, CommandData> allCommands;
    // Command name -> set os subcommands commandata
    private final Map<String, Set<CommandData>> subcommands;
    private final Map<String, CommandData> mainCommands;
    private final BukkitUsageMessageBuilder bukkitUsageMessageBuilder;

    public CommandRegistry(){
        this.allCommands = new HashMap<>();
        this.subcommands = new HashMap<>();
        this.bukkitUsageMessageBuilder = new BukkitUsageMessageBuilder();
        this.mainCommands = new HashMap<>();
    }

    public void put(CommandRunner commandRunnerInstance, Command commandInfo){
        String[] args = commandInfo.args();
        String command = commandInfo.value();
        CommandData commandData = new CommandData( command, commandInfo.canBeTypedInConsole(),
                commandInfo.permissions(), commandInfo.isAsync(), args, commandRunnerInstance,
                commandInfo.helperCommand(), this.bukkitUsageMessageBuilder.build(command, args),
                commandInfo.explanation(),
                commandInfo.isHelper());

        this.allCommands.put(commandInfo.value(), commandData);

        if(commandData.isSubcommand()){
            addToSubCommandList(commandData);
            this.addSubcommandMainCommandToMainCommandList(commandRunnerInstance, commandInfo);
        }else{
            this.mainCommands.put(commandInfo.value(), commandData);
        }
    }

    private void addSubcommandMainCommandToMainCommandList(CommandRunner commandRunnerInstance, Command commandInfo){
        String mainCommandName = commandInfo.value().split(" ")[0];
        String[] args = commandInfo.args();

        this.mainCommands.putIfAbsent(mainCommandName, new CommandData(
                mainCommandName, commandInfo.canBeTypedInConsole(),
                commandInfo.permissions(), commandInfo.isAsync(), args, commandRunnerInstance,
                commandInfo.helperCommand(), this.bukkitUsageMessageBuilder.build(mainCommandName, args),
                commandInfo.explanation(),
                commandInfo.isHelper())
        );
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
        CommandData commandData = this.allCommands.get(commandName);
        boolean commandFound = commandData != null;

        return commandFound ?
                Optional.of(commandData) :
                args.length > 0 ?
                        findSubCommand(commandName, args) :
                        Optional.empty();
    }

    private Optional<CommandData> findSubCommand(String commandName, String[] args){
        CommandData subCommandData = this.allCommands.get(String.format("%s %s", commandName, args[0]));

        return subCommandData != null && subCommandData.isSubcommand() ?
                Optional.of(subCommandData) :
                Optional.empty();
    }

    public Collection<CommandData> getMainCommands() {
        return this.mainCommands.values();
    }
}
