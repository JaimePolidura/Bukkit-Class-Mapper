package es.bukkitclassmapper.commands;

import es.bukkitclassmapper.ClassMapperConfiguration;
import es.bukkitclassmapper.ClassMapper;
import es.bukkitclassmapper._shared.utils.ClassMapperLogger;
import es.bukkitclassmapper.commands.commandrunners.CommandRunner;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Stream;

public final class CommandMapper extends ClassMapper {
    private final DefaultCommandExecutorEntrypoint commandExecutorEntrypoint;
    private final BukkitUsageMessageBuilder bukkitUsageMessageBuilder;
    private final CommandRegistry commandRegistry;

    public CommandMapper(ClassMapperConfiguration configuration, ClassMapperLogger logger) {
        super(configuration, logger);
        this.commandRegistry = new CommandRegistry();
        this.commandExecutorEntrypoint = new DefaultCommandExecutorEntrypoint(configuration, logger, commandRegistry);
        this.bukkitUsageMessageBuilder = new BukkitUsageMessageBuilder();
    }

    @Override
    public void scan () {
        Set<Class<? extends CommandRunner>> commandRunnerClasses = this.getCommandRunnerClasses(
                reflections.getTypesAnnotatedWith(Command.class));

        createInstancesAndSave(commandRunnerClasses);
    }

    private Set<Class<? extends CommandRunner>> getCommandRunnerClasses(Set<Class<?>> classes) {
        Set<Class<? extends CommandRunner>> checkedClasses = new HashSet<>();

        for(Class<?> notCheckedClass : classes){
            if(CommandRunner.class.isAssignableFrom(notCheckedClass))
                checkedClasses.add((Class<? extends CommandRunner>) notCheckedClass);
            else
                logger.error("Couldn't initialize command in class %s. This class should implement command interface", notCheckedClass);
        }

        return checkedClasses;
    }

    private void createInstancesAndSave(Set<Class<? extends CommandRunner>> commandRunnersClasses) {
        for(Class<? extends CommandRunner> classToSave : commandRunnersClasses){
            Command annotation = getCommandAnnotationFromClass(classToSave);

            saveCommand(classToSave, annotation);
        }

        logger.info("Mapped all command classes. Total %s", commandRunnersClasses.size());
    }

    private Command getCommandAnnotationFromClass(Class<? extends CommandRunner> classToFind) {
        return (Command) Stream.of(classToFind.getAnnotations())
                .filter(annotation -> annotation instanceof Command)
                .findAny()
                .get();
    }

    @SneakyThrows
    private void saveCommand(Class<? extends CommandRunner> commandClass, Command commandInfoAnnotation) {
        if(configuration.getInstanceProvider().isExcluded(commandClass)){
            return;
        }
        CommandRunner commandRunnerInstance = configuration.getInstanceProvider().get(commandClass);
        if(commandRunnerInstance == null){
            throw new RuntimeException(String.format("Bukkit command runner %s provided by dependency provider is null", commandClass));
        }

        String commandName = commandInfoAnnotation.value();

        addCommandToRegistry(commandRunnerInstance, commandInfoAnnotation);
        registerCommandBukkit(commandName);

        logger.debug("Registered command /%s on class %s", commandName, commandClass.getName());
    }

    private void addCommandToRegistry(CommandRunner commandRunnerInstance, Command commandInfo){
        String usageMessage = bukkitUsageMessageBuilder.build(commandInfo.value(), commandInfo.args());

        this.commandRegistry.put(new CommandData(commandRunnerInstance,
                commandInfo.permissions(),
                commandInfo.explanation(),
                commandInfo.value(),
                commandInfo.isHelper(),
                commandInfo.args(),
                usageMessage,
                commandInfo.isAsync(),
                commandInfo.needsOp()
        ));
    }

    private void registerCommandBukkit (String commandName) {
        try{
            //Just in case we are passing a subcommand
            Bukkit.getPluginCommand(commandName.split(" ")[0]).setExecutor(commandExecutorEntrypoint);
        }catch (Exception e) {
            throw new NullPointerException(String.format("Command: %s not found", commandName.split(" ")[0]));
        }
    }
}
