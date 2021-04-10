package es.jaimetruman.commands;

import es.jaimetruman.ClassScanner;
import javafx.util.Pair;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Stream;

public final class CommandMapper extends ClassScanner {
    //Command name - pair: commandRunner, command annotation (description)
    private final Map<String, Pair<CommandRunner, Command>> mappedCommands;
    private final CommandExecutor commandExecutor;

    public CommandMapper(String packageToStartScanning, String messageOnCommandNotFound, String messageOnWrongSender) {
        this(packageToStartScanning, messageOnCommandNotFound, messageOnWrongSender, ChatColor.DARK_RED + "You dont have any permissions to execute that command");
    }

    public CommandMapper(String packageToStartScanning, String messageOnCommandNotFound, String messageOnWrongSender, String onWrongPermissions) {
        super(packageToStartScanning);

        this.mappedCommands = new HashMap<>();
        this.commandExecutor = new DefaultCommandExcutorEntrypoint(messageOnWrongSender, messageOnCommandNotFound, onWrongPermissions);

        this.scan();
    }

    @Override
    public void scan () {
        Set<Class<? extends CommandRunner>> checkedClasses = this.checkIfClassesImplementsCommandInterface(
                reflections.getTypesAnnotatedWith(Command.class));

        this.createInstancesAndAdd(checkedClasses);
    }

    private Set<Class<? extends CommandRunner>> checkIfClassesImplementsCommandInterface(Set<Class<?>> classes) {
        Set<Class<? extends CommandRunner>> checkedClasses = new HashSet<>();

        for(Class<?> notCheckedClass : classes){
            if(CommandRunner.class.isAssignableFrom(notCheckedClass)){
                checkedClasses.add((Class<? extends CommandRunner>) notCheckedClass);
            }else{
                System.out.println("Couldn't initialize command in class " + notCheckedClass + ". This class should implement command interface");
            }
        }

        return checkedClasses;
    }

    private void createInstancesAndAdd (Set<Class<? extends CommandRunner>> classes) {
        for(Class<? extends CommandRunner> classToAdd : classes){
            Command annotation = this.getCommandExecutorAnnotationFromClass(classToAdd);

            saveCommand(classToAdd, annotation);
        }

        System.out.println("Mapped all command classes");
    }

    private Command getCommandExecutorAnnotationFromClass (Class<? extends CommandRunner> classToFind) {
        return (Command) Stream.of(classToFind.getAnnotations())
                .filter(annotation -> annotation instanceof Command)
                .findAny()
                .get();
    }

    @SneakyThrows
    private void saveCommand (Class<? extends CommandRunner> commandClass, Command annotation) {
        CommandRunner command = commandClass.newInstance();
        String commandName = annotation.name();

        this.registerCommandBukkit(commandName);

        this.mappedCommands.put(commandName, new Pair<>(command, annotation));
    }

    private void registerCommandBukkit (String commandName) {
        //Just in case we are passing a subcommand
        Bukkit.getPluginCommand(commandName.split(" ")[0]).setExecutor(commandExecutor);
    }

    private Optional<Pair<CommandRunner, Command>> findByName (String commandName, String[] args) {
        Pair<CommandRunner, Command> command = this.mappedCommands.get(commandName);

        if(command != null){
            return Optional.of(command);
        }else if (args == null || args.length == 0) {
            return Optional.empty();
        }

        //Maybe he typed a subcommand.
        Pair<CommandRunner, Command> subCommand = this.mappedCommands.get(String.format("%s %s", commandName, args[0]));

        if(subCommand != null){
            return Optional.ofNullable(subCommand);
        }else{
            return Optional.empty();
        }
    }

    private final class DefaultCommandExcutorEntrypoint implements CommandExecutor {
        private final String messageOnWrongSender;
        private final String messageOnCommandNotFound;
        private final String messageOnNotHavePermissions;

        public DefaultCommandExcutorEntrypoint(String messageOnWrongSender, String messageOnCommandNotFound, String messageOnNotHavePermissions) {
            this.messageOnWrongSender = messageOnWrongSender;
            this.messageOnCommandNotFound = messageOnCommandNotFound;
            this.messageOnNotHavePermissions = messageOnNotHavePermissions;
        }

        @Override
        public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
            Optional<Pair<CommandRunner, Command>> optionalCommandRunner = findByName(command.getName(), args);

            if(!optionalCommandRunner.isPresent()){
                sender.sendMessage(messageOnCommandNotFound);
                return true;
            }

            Command commandData = optionalCommandRunner.get().getValue();
            CommandRunner commandRunner = optionalCommandRunner.get().getKey();

            if(!(sender instanceof Player) && !commandData.canBeTypedInConsole()){
                sender.sendMessage(messageOnWrongSender);
                return true;
            }
            if(!commandData.permissions().equals("") && !sender.hasPermission(commandData.permissions())){
                sender.sendMessage(messageOnNotHavePermissions);
                return true;
            }

            commandRunner.execute(sender, args);

            return true;
        }
    }
}
