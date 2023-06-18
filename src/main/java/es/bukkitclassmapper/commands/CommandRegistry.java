package es.bukkitclassmapper.commands;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class CommandRegistry {
    private final Map<String, Set<CommandData>> subcommands;
    private final Map<String, CommandData> mainCommands;
    private final Map<String, CommandData> allCommands;

    public CommandRegistry(){
        this.allCommands = new ConcurrentHashMap<>();
        this.subcommands = new ConcurrentHashMap<>();
        this.mainCommands = new ConcurrentHashMap<>();
    }

    public void put(CommandData commandData){
        String commandName = commandData.getCommand();

        allCommands.put(commandName, commandData);

        if(commandData.isSubcommand()) {
            addToSubCommandList(commandData);
        }
        if(commandData.isMainCommand() || commandData.isSubCommandHelper()){
            mainCommands.put(commandData.getCommand(), commandData);
        }
    }

    public Optional<CommandData> findSubcommandByMainCommandName(String commandName, String[] args) {
        return args.length > 0 ? subcommands.get(commandName).stream()
                .filter(subcommand -> args[0].equalsIgnoreCase("help") ||
                                      subcommand.getSubCommand().equalsIgnoreCase(args[0]))
                .findFirst() :
                Optional.empty();
    }

    public Set<CommandData> findSubcommandsByCommandName(String mainCommand){
        return this.subcommands.get(mainCommand);
    }

    private void addToSubCommandList(CommandData commandData) {
        String mainCommand = commandData.getMainCommand();

        this.subcommands.putIfAbsent(mainCommand, new HashSet<>());
        this.subcommands.get(mainCommand).add(commandData);
    }

    public Optional<CommandData> findMainCommandByName(String commandName) {
        return Optional.ofNullable(this.mainCommands.get(commandName));
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
