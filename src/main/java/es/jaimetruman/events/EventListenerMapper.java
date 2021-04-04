package es.jaimetruman.events;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.Set;


public final class EventListenerMapper {
    private final String packageToScan;
    private final Reflections reflections;
    private final Plugin plugin;

    public static EventListenerMapper startScanning (String packageToStart, Plugin plugin) {
        return new EventListenerMapper(packageToStart, plugin);
    }

    public EventListenerMapper(String packageToScan, Plugin plugin) {
        this.packageToScan = packageToScan;
        this.plugin = plugin;

        this.reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packageToScan))
                .setScanners(new TypeAnnotationsScanner(),
                             new SubTypesScanner()));

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
