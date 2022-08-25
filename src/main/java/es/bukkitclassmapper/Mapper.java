package es.bukkitclassmapper;


import es.bukkitclassmapper._shared.utils.InstanceProvider;
import es.bukkitclassmapper.commands.CommandMapper;
import es.bukkitclassmapper.commands.CommandRegistry;
import es.bukkitclassmapper.commands.DefaultCommandExecutorEntrypoint;
import es.bukkitclassmapper.events.EventListenerMapper;
import es.bukkitclassmapper.mobs.MobMapper;
import es.bukkitclassmapper.task.TaskMapper;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class Mapper  {
    private final Set<ClassScanner> mappers;
    private final String commonPackage;
    private final Plugin plugin;
    private InstanceProvider instanceProvider;

    private Mapper(String commonPackage, Plugin plugin, InstanceProvider instanceProvider) {
        this.instanceProvider = instanceProvider;
        this.mappers = new HashSet<>();
        this.commonPackage = commonPackage;
        this.plugin = plugin;
    }

    public Mapper all (String onWrongCommand, String onWrongPermissions, InstanceProvider instanceProvider) {
        this.commandMapper(onWrongCommand, onWrongPermissions);
        this.taskMapper();
        this.eventListenerMapper();
        this.mobMapper();
        this.instanceProvider = instanceProvider;

        return this;
    }


    public Mapper all (String onWrongCommand, String onWrongPermissions) {
        this.commandMapper(onWrongCommand, onWrongPermissions);
        this.taskMapper();
        this.eventListenerMapper();
        this.mobMapper();
        this.instanceProvider = InstanceProvider.defaultEmpty();

        return this;
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

    public Mapper commandMapper (String onWrongCommand, String onWrongPermissions) {
        CommandRegistry commandRegistry = new CommandRegistry();
        DefaultCommandExecutorEntrypoint defaultCommandExecutorEntrypoint = new DefaultCommandExecutorEntrypoint(
                commandRegistry, onWrongCommand, plugin, onWrongPermissions);

        this.mappers.add(new CommandMapper(commonPackage, defaultCommandExecutorEntrypoint, commandRegistry));

        return this;
    }

    public Mapper instanceProvider(InstanceProvider instanceProvider) {
        this.instanceProvider = instanceProvider;
        return this;
    }

    public void startScanning () {
        this.mappers.forEach(mapper -> mapper.scan(this.instanceProvider));
    }

    public static Mapper build (String commonPackage, Plugin plugin) {
        return new Mapper(commonPackage, plugin, InstanceProvider.defaultEmpty());
    }

    public static Mapper build (String commonPackage, Plugin plugin, InstanceProvider instanceProvider) {
        return new Mapper(commonPackage, plugin, instanceProvider);
    }

    public static Mapper build (Plugin plugin) {
        return new Mapper(plugin.getClass().getPackage().getName(), plugin, InstanceProvider.defaultEmpty());
    }

    public static Mapper build (Plugin plugin, InstanceProvider instanceProvider) {
        return new Mapper(plugin.getClass().getPackage().getName(), plugin, instanceProvider);
    }
}
