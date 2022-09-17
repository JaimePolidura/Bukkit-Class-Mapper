package es.bukkitclassmapper.commands;

import es.bukkitclassmapper.ClassMapperConfiguration;
import es.bukkitclassmapper.ClassMapper;
import es.bukkitclassmapper.commands.commandrunners.CommandRunner;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Stream;

public final class CommandMapper extends ClassMapper {
    private final DefaultCommandExecutorEntrypoint commandExecutorEntrypoint;
    private final BukkitUsageMessageBuilder bukkitUsageMessageBuilder;
    private final CommandRegistry commandRegistry;

    public CommandMapper(ClassMapperConfiguration configuration) {
        super(configuration);
        this.commandRegistry = new CommandRegistry();
        this.bukkitUsageMessageBuilder = new BukkitUsageMessageBuilder();
        this.commandExecutorEntrypoint = new DefaultCommandExecutorEntrypoint(configuration);
    }

    @Override
    public void scan () {
        Set<Class<? extends CommandRunner>> commandRunnerClasses = this.getCommandRunnerClasses(
                reflections.getTypesAnnotatedWith(Command.class));

        this.createInstancesAndSave(commandRunnerClasses);
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

    private void createInstancesAndSave(Set<Class<? extends CommandRunner>> commandRunnersClasses) {
        for(Class<? extends CommandRunner> classToSave : commandRunnersClasses){
            Command annotation = this.getCommandAnnotationFromClass(classToSave);

            saveCommand(classToSave, annotation);
        }
    }

    private Command getCommandAnnotationFromClass(Class<? extends CommandRunner> classToFind) {
        return (Command) Stream.of(classToFind.getAnnotations())
                .filter(annotation -> annotation instanceof Command)
                .findAny()
                .get();
    }

    @SneakyThrows
    private void saveCommand(Class<? extends CommandRunner> commandClass, Command commandInfoAnnotation) {
        CommandRunner commandRunnerInstance = this.configuration.getInstanceProvider().get(commandClass);
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
