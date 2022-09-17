package es.bukkitclassmapper.events;

import es.bukkitclassmapper.ClassMapperConfiguration;
import es.bukkitclassmapper.ClassMapper;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.Set;


public final class EventListenerMapper extends ClassMapper {
    public EventListenerMapper(ClassMapperConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void scan () {
        Set<Class<? extends Listener>> classImplemensListener = this.reflections.getSubTypesOf(Listener.class);

        for(Class<? extends Listener> classListener : classImplemensListener){
            Listener newInstance = this.configuration.getInstanceProvider().get(classListener);

            Bukkit.getPluginManager().registerEvents(newInstance, this.configuration.getPlugin());
        }

        System.out.println("Mapped all event listener classes");
    }
}
