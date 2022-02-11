package es.jaimetruman.commands;

import es.jaimetruman.ClassScanner;
import javafx.util.Pair;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Stream;

public final class CommandMapper extends ClassScanner {
    //Command name - pair: commandRunner, command annotation (description)
    private final CommandExecutor commandExecutor;
    private final Plugin plugin;
    private final CommandRegistry commandRegistry;

    public CommandMapper(String packageToStartScanning, String messageOnCommandNotFound, String messageOnWrongSender, Plugin plugin) {
        this(packageToStartScanning, messageOnCommandNotFound, messageOnWrongSender, ChatColor.DARK_RED + "You dont have any permissions to execute that command", plugin);
    }

    public CommandMapper(String packageToStartScanning, String messageOnCommandNotFound, String messageOnWrongSender, String onWrongPermissions, Plugin plugin) {
        super(packageToStartScanning);

        this.commandExecutor = new DefaultCommandExcutorEntrypoint(messageOnWrongSender, messageOnCommandNotFound, onWrongPermissions);
        this.plugin = plugin;
        this.commandRegistry = new CommandRegistry();
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
        CommandRunner commandRunnerInstance = commandClass.newInstance();
        String commandName = annotation.value();

        this.registerCommandBukkit(commandName);

        this.commandRegistry.put(commandRunnerInstance, annotation);
    }

    private void registerCommandBukkit (String commandName) {
        //Just in case we are passing a subcommand
        Bukkit.getPluginCommand(commandName.split(" ")[0]).setExecutor(commandExecutor);
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
            Optional<Pair<CommandRunner, Command>> optionalCommandRunner = commandRegistry.findByName(command.getName(), args);

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

            if(commandData.isAsync()) {
                Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, () -> commandRunner.execute(sender, args), 0L);
            }else{
                commandRunner.execute(sender, args);
            }

            return true;
        }
    }
}
