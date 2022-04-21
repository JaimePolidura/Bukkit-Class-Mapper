package es.jaimetruman.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class CommandRegistry {
    //Command name -> <instance of commandrunner, command info>
    private final Map<String, CommandData> commands;

    public CommandRegistry(){
        this.commands = new HashMap<>();
    }

    public void put(CommandRunner commandRunnerInstance, Command commandInfo){
        this.commands.put(commandInfo.value(), new CommandData(
                commandInfo.value(), commandInfo.canBeTypedInConsole(), commandInfo.permissions(),
                commandInfo.isAsync(), commandInfo.args(), commandRunnerInstance
        ));
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
