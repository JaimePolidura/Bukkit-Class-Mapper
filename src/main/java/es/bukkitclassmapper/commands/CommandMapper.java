package es.bukkitclassmapper.commands;

import es.bukkitclassmapper.ClassScanner;
import es.bukkitclassmapper._shared.utils.reflections.InstanceProvider;
import es.bukkitclassmapper.commands.commandrunners.CommandRunner;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Stream;

public final class CommandMapper extends ClassScanner {
    private final DefaultCommandExecutorEntrypoint commandExecutorEntrypoint;
    private final CommandRegistry commandRegistry;
    private final BukkitUsageMessageBuilder bukkitUsageMessageBuilder;

    public CommandMapper(String packageToStartScanning, DefaultCommandExecutorEntrypoint defaultCommandExecutorEntrypoint,
                         CommandRegistry commandRegistry) {
        super(packageToStartScanning);

        this.bukkitUsageMessageBuilder = new BukkitUsageMessageBuilder();
        this.commandRegistry = commandRegistry;
        this.commandExecutorEntrypoint = defaultCommandExecutorEntrypoint;
    }

    @Override
    public void scan (InstanceProvider instanceProvider) {
        Set<Class<? extends CommandRunner>> commandRunnerClasses = this.getCommandRunnerClasses(
                reflections.getTypesAnnotatedWith(Command.class));

        this.createInstancesAndSave(commandRunnerClasses, instanceProvider);
    }

    private Set<Class<? extends CommandRunner>> getCommandRunnerClasses(Set<Class<?>> classes) {
        Set<Class<? extends CommandRunner>> checkedClasses = new HashSet<>();

        for(Class<?> notCheckedClass : classes){
            if(CommandRunner.class.isAssignableFrom(notCheckedClass))
                checkedClasses.add((Class<? extends CommandRunner>) notCheckedClass);
            else
                System.out.println("Couldn't initialize command in class " + notCheckedClass + ". This class should implement command interface");
        }

        return checkedClasses;
    }

    private void createInstancesAndSave(Set<Class<? extends CommandRunner>> commandRunnersClasses, InstanceProvider instanceProvider) {
        for(Class<? extends CommandRunner> classToSave : commandRunnersClasses){
            Command annotation = this.getCommandAnnotationFromClass(classToSave);

            saveCommand(classToSave, annotation, instanceProvider);
        }
    }

    private Command getCommandAnnotationFromClass(Class<? extends CommandRunner> classToFind) {
        return (Command) Stream.of(classToFind.getAnnotations())
                .filter(annotation -> annotation instanceof Command)
                .findAny()
                .get();
    }

    @SneakyThrows
    private void saveCommand(Class<? extends CommandRunner> commandClass, Command commandInfoAnnotation, InstanceProvider instanceProvider) {
        CommandRunner commandRunnerInstance = instanceProvider.get(commandClass);
        String commandName = commandInfoAnnotation.value();

        this.addCommandToRegistry(commandRunnerInstance, commandInfoAnnotation);
        this.registerCommandBukkit(commandName);
    }

    private void addCommandToRegistry(CommandRunner commandRunnerInstance, Command commandInfo){
        String usageMessage = this.bukkitUsageMessageBuilder.build(commandInfo.value(), commandInfo.args());

        this.commandRegistry.put(new CommandData(commandInfo.value(), commandInfo.canBeTypedInConsole(),
                commandInfo.permissions(), commandInfo.isAsync(), commandInfo.args(), commandRunnerInstance,
                usageMessage, commandInfo.helperCommand(), commandInfo.explanation(), commandInfo.isHelper()));
    }

    private void registerCommandBukkit (String commandName) {
        try{
            //Just in case we are passing a subcommand
            Bukkit.getPluginCommand(commandName.split(" ")[0]).setExecutor(this.commandExecutorEntrypoint);
        }catch (Exception e) {
            throw new NullPointerException(String.format("Command: %s not found", commandName.split(" ")[0]));
        }
    }
}
