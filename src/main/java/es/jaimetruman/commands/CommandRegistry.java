package es.jaimetruman.commands;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandRegistry {
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
                args.length > 0 ? //Maybe it is a subcommand
                        Optional.ofNullable(this.commands.get(String.format("%s %s", commandName, args[0]))) :
                        Optional.empty();
    }
}
