package es.jaimetruman.commands;

import es.jaimetruman.ClassScanner;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Stream;

public final class CommandMapper extends ClassScanner {
    //Command name - pair: commandRunner, command annotation (description)
    private final DefaultCommandExecutorEntrypoint commandExecutorEntrypoint;
    private final CommandRegistry commandRegistry;

    public CommandMapper(String packageToStartScanning, DefaultCommandExecutorEntrypoint defaultCommandExecutorEntrypoint,
                         CommandRegistry commandRegistry) {
        super(packageToStartScanning);

        this.commandRegistry = commandRegistry;
        this.commandExecutorEntrypoint = defaultCommandExecutorEntrypoint;
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
            if(CommandRunner.class.isAssignableFrom(notCheckedClass)){
                checkedClasses.add((Class<? extends CommandRunner>) notCheckedClass);
            }else{
                System.out.println("Couldn't initialize command in class " + notCheckedClass + ". This class should implement command interface");
            }
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
    private void saveCommand (Class<? extends CommandRunner> commandClass, Command annotation) {
        CommandRunner commandRunnerInstance = commandClass.newInstance();
        String commandName = annotation.value();

        this.registerCommandBukkit(commandName);

        this.commandRegistry.put(commandRunnerInstance, annotation);
    }

    private void registerCommandBukkit (String commandName) {
        //Just in case we are passing a subcommand
        Bukkit.getPluginCommand(commandName.split(" ")[0]).setExecutor(this.commandExecutorEntrypoint);
    }
}
