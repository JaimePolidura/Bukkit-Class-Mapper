package es.jaimetruman.events;

import es.jaimetruman.Mapper;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Set;


public final class EventListenerMapper extends Mapper {
    private final String packageToScan;
    private final Plugin plugin;

    public static EventListenerMapper startScanning (String packageToStart, Plugin plugin) {
        return new EventListenerMapper(packageToStart, plugin);
    }

    public EventListenerMapper(String packageToScan, Plugin plugin) {
        super(packageToScan);

        this.packageToScan = packageToScan;
        this.plugin = plugin;

        this.startScanning();
    }

    private void startScanning () {
        Set<Class<? extends Listener>> classImplemensListener = this.reflections.getSubTypesOf(Listener.class);

        for(Class<? extends Listener> classListener : classImplemensListener){
            try {
                Listener newInstance = classListener.newInstance();

                Bukkit.getPluginManager().registerEvents(newInstance, this.plugin);
            } catch (InstantiationException | IllegalAccessException e) {
                System.err.printf("Error with %s, this class needs to have an empty constructor!", classListener.getName());
            }
        }
    }
}
