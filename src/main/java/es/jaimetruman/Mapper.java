package es.jaimetruman;


import es.jaimetruman.commands.CommandMapper;
import es.jaimetruman.events.EventListenerMapper;
import es.jaimetruman.mobs.MobMapper;
import es.jaimetruman.task.TaskMapper;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public final class Mapper  {
    private final Set<ClassScanner> mappers;
    private final String commonPackage;
    private final Plugin plugin;

    private Mapper (String commonPackage, Plugin plugin) {
        this.mappers = new HashSet<>();
        this.commonPackage = commonPackage;
        this.plugin = plugin;
    }

    public Mapper mobMapper () {
        this.mappers.add(new MobMapper(commonPackage, plugin));

        return this;
    }

    public Mapper eventListenerMapper () {
        this.mappers.add(new EventListenerMapper(commonPackage, plugin));

        return this;
    }

    public Mapper taskMapper () {
        this.mappers.add(new TaskMapper(commonPackage, plugin));

        return this;
    }

    public Mapper commandMapper (String onWrongCommand, String onWrongSender, String onWrongPermissions) {
        this.mappers.add(new CommandMapper(commonPackage, onWrongCommand, onWrongSender, onWrongPermissions));

        return this;
    }

    public Mapper commandMapper (String onWrongCommand, String onWrongSender) {
        this.mappers.add(new CommandMapper(commonPackage, onWrongCommand, onWrongSender));

        return this;
    }

    public void startScanning () {
        this.mappers.forEach(ClassScanner::scan);
    }

    public static Mapper build (String commonPackage, Plugin plugin) {
        return new Mapper(commonPackage, plugin);
    }
}
