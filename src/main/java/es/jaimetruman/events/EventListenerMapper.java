package es.jaimetruman.events;

import es.jaimetruman.ClassScanner;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Set;


public final class EventListenerMapper extends ClassScanner {
    private final String packageToScan;
    private final Plugin plugin;

    public EventListenerMapper(String packageToScan, Plugin plugin) {
        super(packageToScan);

        this.packageToScan = packageToScan;
        this.plugin = plugin;
    }

    @Override
    public void scan () {
        Set<Class<? extends Listener>> classImplemensListener = this.reflections.getSubTypesOf(Listener.class);

        for(Class<? extends Listener> classListener : classImplemensListener){
            try {
                Listener newInstance = classListener.newInstance();

                Bukkit.getPluginManager().registerEvents(newInstance, this.plugin);
            } catch (InstantiationException | IllegalAccessException e) {
                //Ignored
            }
        }

        System.out.println("Mapped all event listener classes");
    }
}
