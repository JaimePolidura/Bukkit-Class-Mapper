package es.jaimetruman.commands;

import javafx.util.Pair;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.*;
import java.util.stream.Stream;

public final class CommandMapper {
    //Command name - pair: commandRunner, command annotation (description)
    private final Map<String, Pair<CommandRunner, Command>> mappedCommands;
    private final Reflections reflections;
    private final CommandExecutor commandExecutor;
    private final String packageToStartScanning;

    public static CommandMapper create (String packageToStartScanning, String onWrongSender) {
        return new CommandMapper(packageToStartScanning, onWrongSender);
    }

    private CommandMapper(String packageToStartScanning, String onWrongSender) {
        this.mappedCommands = new HashMap<>();
        this.commandExecutor = new DefaultCommandExcutorEntrypoint(onWrongSender);

        this.packageToStartScanning = packageToStartScanning;

        this.reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packageToStartScanning))
                .setScanners(new TypeAnnotationsScanner(),
                             new SubTypesScanner()));

        this.scanForCommands();
    }

    private void scanForCommands () {
        Set<Class<? extends CommandRunner>> checkedClasses = this.checkIfClassesExtendsCommand(
                reflections.getTypesAnnotatedWith(Command.class));

        this.createInstancesAndAdd(checkedClasses);
    }

    private Set<Class<? extends CommandRunner>> checkIfClassesExtendsCommand (Set<Class<?>> classes) {
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

    public Optional<Pair<CommandRunner, Command>> findByName (String commandName, String[] args) {
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

    class DefaultCommandExcutorEntrypoint implements CommandExecutor {
        private final String messageOnWrongSender;

        public DefaultCommandExcutorEntrypoint(String messageOnWrongSender) {
            this.messageOnWrongSender = messageOnWrongSender;
        }

        @Override
        public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
            Optional<Pair<CommandRunner, Command>> optionalCommandRunner = findByName(command.getName(), args);

            if (optionalCommandRunner.isPresent()) {
                Command commandData = optionalCommandRunner.get().getValue();
                CommandRunner commandRunner = optionalCommandRunner.get().getKey();

                if(!(sender instanceof Player) && !commandData.canBeTypedInConsole()){
                    sender.sendMessage(messageOnWrongSender);
                }else{
                    commandRunner.execute(sender, args);
                }

            } else {
                sender.sendMessage(messageOnWrongSender);
            }

            return true;
        }
    }
}
