package es.bukkitclassmapper.events;

import es.bukkitclassmapper.ClassMapperConfiguration;
import es.bukkitclassmapper.ClassMapper;
import es.bukkitclassmapper._shared.utils.ClassMapperLogger;
import es.bukkitclassmapper.mobs.MobMapper;
import es.jaime.javaddd.domain.exceptions.ResourceNotFound;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.Set;


public final class EventListenerMapper extends ClassMapper {
    public EventListenerMapper(ClassMapperConfiguration configuration, ClassMapperLogger logger) {
        super(configuration, logger);
    }

    @Override
    public void scan () {
        Set<Class<? extends Listener>> classImplemensListener = this.reflections.getSubTypesOf(Listener.class);

        for(Class<? extends Listener> classListener : classImplemensListener){
            Listener newInstance = this.configuration.getInstanceProvider().get(classListener);
            if(classListener.equals(MobMapper.DefaultEntrypointPlayerInteractEntity.class))
                continue;
            if(newInstance == null)
                throw new ResourceNotFound(String.format("Bukkit event listener %s provided by dependency provider is null", classListener));

            Bukkit.getPluginManager().registerEvents(newInstance, this.configuration.getPlugin());

            logger.debug("Registered event listener class %s", classListener.getName());
        }

        logger.info("Mapped all event listener classes. Total: %s", classImplemensListener.size());
    }
}
