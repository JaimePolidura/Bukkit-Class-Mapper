package es.jaimetruman.commands;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class CommandRegistry {
    //Command name -> <instance of commandrunner, command info>
    private final Map<String, Pair<CommandRunner, Command>> commands;

    public CommandRegistry(){
        this.commands = new HashMap<>();
    }

    public void put(CommandRunner commandRunnerInstance, Command commandInfo){
        this.commands.put(commandInfo.value(), new Pair<>(commandRunnerInstance, commandInfo));
    }

    public Optional<Pair<CommandRunner, Command>> findByName(String commandName, String[] args){
        Pair<CommandRunner, Command> command = this.commands.get(commandName);
        boolean commandFound = command != null;

        return commandFound ?
                Optional.of(command) :
                args.length > 0 ?
                        findSubCommand(commandName, args) :
                        Optional.empty();
    }

    private Optional<Pair<CommandRunner, Command>> findSubCommand(String commandName, String[] args){
        Pair<CommandRunner, Command> subCommandData = this.commands.get(String.format("%s %s", commandName, args[0]));

        return subCommandData != null && subCommandData.getValue().isSubCommand() ?
                Optional.of(subCommandData) :
                Optional.empty();
    }
}
