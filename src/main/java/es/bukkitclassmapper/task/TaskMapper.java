package es.bukkitclassmapper.task;

import es.bukkitclassmapper.ClassMapperConfiguration;
import es.bukkitclassmapper.ClassMapper;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class TaskMapper extends ClassMapper {
    public TaskMapper(ClassMapperConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void scan() {
        Set<Class<? extends TaskRunner>> checkedClasses = this.checkIfClassesImplementsMobInterface(
                reflections.getTypesAnnotatedWith(Task.class));

        this.createInstancesAndAdd(checkedClasses);
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

    private void createInstancesAndAdd(Set<Class<? extends TaskRunner>> classes) {
        for(Class<? extends TaskRunner> classToAdd : classes){
            Task annotation = this.getMobExecutorAnnotationFromClass(classToAdd);
            TaskRunner taskRunner = this.configuration.getInstanceProvider().get(classToAdd);

            Bukkit.getScheduler().runTaskTimer(this.configuration.getPlugin(), taskRunner,
                    annotation.delay(), annotation.value());
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
