package es.bukkitclassmapper.task;

import es.bukkitclassmapper.ClassMapperConfiguration;
import es.bukkitclassmapper.ClassMapper;
import es.bukkitclassmapper._shared.utils.ClassMapperLogger;
import es.jaime.javaddd.domain.exceptions.ResourceNotFound;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class TaskMapper extends ClassMapper {
    public TaskMapper(ClassMapperConfiguration configuration, ClassMapperLogger logger) {
        super(configuration, logger);
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
                logger.error("Couldn't initialize task in class %s. This class should implement TaskRunner interface", notCheckedClass);
            }
        }

        return checkedClasses;
    }

    private void createInstancesAndAdd(Set<Class<? extends TaskRunner>> classes) {
        for(Class<? extends TaskRunner> classToAdd : classes){
            if(configuration.getInstanceProvider().isExcluded(classToAdd)) {
                return;
            }
            Task annotation = this.getMobExecutorAnnotationFromClass(classToAdd);
            TaskRunner taskRunner = this.configuration.getInstanceProvider().get(classToAdd);
            if(taskRunner == null){
                throw new ResourceNotFound(String.format("Bukkit task runnner %s provided by dependency provider is null", classToAdd));
            }

            Bukkit.getScheduler().runTaskTimer(this.configuration.getPlugin(), taskRunner,
                    annotation.delay(), annotation.value());

            logger.debug("Registered task class %s", classToAdd.getName());
        }

        logger.info("Mapped all task classes. Total %s", classes.size());
    }

    private Task getMobExecutorAnnotationFromClass(Class<? extends TaskRunner> classToFind) {
        return (Task) Stream.of(classToFind.getAnnotations())
                .filter(annotation -> annotation instanceof Task)
                .findAny()
                .get();
    }
}
