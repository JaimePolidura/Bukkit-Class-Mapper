package es.jaimetruman.commands;

import java.util.*;

public final class CommandRegistry {
    private final Map<String, CommandData> allCommands;
    private final Map<String, Set<CommandData>> subcommands;
    private final Map<String, CommandData> mainCommands;

    private final BukkitUsageMessageBuilder bukkitUsageMessageBuilder;

    public CommandRegistry(){
        this.allCommands = new HashMap<>();
        this.subcommands = new HashMap<>();
        this.bukkitUsageMessageBuilder = new BukkitUsageMessageBuilder();
        this.mainCommands = new HashMap<>();
    }

    public void put(CommandData commandData){
        String commandName = commandData.getCommand();

        this.allCommands.put(commandName, commandData);

        if(commandData.isSubcommand()){
            addToSubCommandList(commandData);
            this.addSubcommandMainCommandToMainCommandList(commandData);
        }else{
            this.mainCommands.put(commandName, commandData);
        }
    }

    private void addSubcommandMainCommandToMainCommandList(CommandData commandData){
        String mainCommandName = commandData.getCommand().split(" ")[0];
        String[] args = commandData.getArgs();

        this.mainCommands.putIfAbsent(mainCommandName, new CommandData(
                mainCommandName, commandData.canBeTypedInConsole(),
                commandData.getPermissions(), commandData.isAsync(), args, commandData.getRunner(),
                commandData.getHelperCommand(), this.bukkitUsageMessageBuilder.build(mainCommandName, args),
                commandData.getExplanation(), commandData.isHelper())
        );
    }

    public Set<CommandData> findSubcommandsByCommandName(String mainCommand){
        return this.subcommands.get(mainCommand);
    }

    private void addToSubCommandList(CommandData commandData) {
        String mainCommand = commandData.getCommand().split(" ")[0];

        this.subcommands.putIfAbsent(mainCommand, new HashSet<>());
        this.subcommands.get(mainCommand).add(commandData);
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
