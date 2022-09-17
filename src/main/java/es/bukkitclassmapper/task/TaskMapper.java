package es.bukkitclassmapper.task;

import es.bukkitclassmapper.ClassScanner;
import es.bukkitclassmapper._shared.utils.reflections.InstanceProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class TaskMapper extends ClassScanner {
    private final Plugin plugin;

    public TaskMapper(String packageToStartScanning, Plugin plugin) {
        super(packageToStartScanning);
        this.plugin = plugin;
    }

    @Override
    public void scan(InstanceProvider instanceProvider) {
        Set<Class<? extends TaskRunner>> checkedClasses = this.checkIfClassesImplementsMobInterface(
                reflections.getTypesAnnotatedWith(Task.class));

        this.createInstancesAndAdd(checkedClasses, instanceProvider);
    }

    private Set<Class<? extends TaskRunner>> checkIfClassesImplementsMobInterface(Set<Class<?>> classes) {
        Set<Class<? extends TaskRunner>> checkedClasses = new HashSet<>();

        for(Class<?> notCheckedClass : classes){
            if(TaskRunner.class.isAssignableFrom(notCheckedClass)){
                checkedClasses.add((Class<? extends TaskRunner>) notCheckedClass);
            }else{
                System.out.println("Couldn't initialize task in class " + notCheckedClass + ". This class should implement TaskRunner interface");
            }
        }

        return checkedClasses;
    }

    private void createInstancesAndAdd(Set<Class<? extends TaskRunner>> classes, InstanceProvider instanceProvider) {
        for(Class<? extends TaskRunner> classToAdd : classes){
            Task annotation = this.getMobExecutorAnnotationFromClass(classToAdd);
            TaskRunner taskRunner = instanceProvider.get(classToAdd);

            Bukkit.getScheduler().runTaskTimer(plugin, taskRunner, annotation.delay(), annotation.value());
        }

        System.out.println("Mapped all task classes");
    }

    private Task getMobExecutorAnnotationFromClass(Class<? extends TaskRunner> classToFind) {
        return (Task) Stream.of(classToFind.getAnnotations())
                .filter(annotation -> annotation instanceof Task)
                .findAny()
                .get();
    }
}
