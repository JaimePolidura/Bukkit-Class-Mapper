package es.bukkitclassmapper;

import es.bukkitclassmapper._shared.utils.ClassMapperLogger;
import es.bukkitclassmapper._shared.utils.reflections.BukkitClassMapperInstanceProvider;
import es.bukkitclassmapper.commands.CommandMapper;
import es.bukkitclassmapper.events.EventListenerMapper;
import es.bukkitclassmapper.mobs.MobMapper;
import es.bukkitclassmapper.task.TaskMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import static es.bukkitclassmapper._shared.utils.ExceptionUtils.*;

@AllArgsConstructor
public final class ClassMapperConfiguration {
    public static ClassMapperConfiguration INSTANCE = null; //TODO Improve

    @Getter private final Plugin plugin;
    @Getter private final String commonPackage;
    @Getter private final BukkitClassMapperInstanceProvider instanceProvider;
    @Getter private final Set<Class<? extends ClassMapper>> mappers;
    @Getter private final Executor threadPool;
    @Getter private final boolean waitUntilCompletion;
    @Getter private final String onWrongPermissions;
    @Getter private final String onCommandNotFound;
    @Getter private final boolean useDebugLogging;
    @Getter private final Reflections reflections;
    @Getter private       boolean printExceptions;

    @SneakyThrows
    public ClassMapperConfiguration startScanning() {
        INSTANCE = this;

        CountDownLatch mappersCompleted = new CountDownLatch(this.mappers.size());
        ClassMapperLogger classMapperLogger = new ClassMapperLogger(this);

        this.mappers.stream()
                .map(mapperClass -> runAndGetOrTerminate(() -> mapperClass.getConstructors()[0].newInstance(this, classMapperLogger)))
                .map(mapperInstance -> (ClassMapper) mapperInstance)
                .forEach(mapperInstance -> {
                    threadPool.execute(mapperInstance::scan);
                    mappersCompleted.countDown();
                });

        if(this.waitUntilCompletion) {
            mappersCompleted.await();
        }

        return this;
    }

    public void setPrintExceptions(boolean printExceptions) {
        this.printExceptions = printExceptions;
    }

    public static ClassMapperConfigurationBuilder builder(Plugin plugin, String commonPackage) {
        return new ClassMapperConfigurationBuilder(plugin, commonPackage);
    }

    public static class ClassMapperConfigurationBuilder {
        private BukkitClassMapperInstanceProvider instanceProvider;
        private Set<Class<? extends ClassMapper>> mappers;
        private boolean waitUntilCompletion;
        private final String commonPackage;
        private String onWrongPermissions;
        private String onCommandNotFound;
        private boolean useDebugLogging;
        private Reflections reflections;
        private Executor threadPool;
        private final Plugin plugin;
        private boolean printExceptions;

        public ClassMapperConfigurationBuilder(Plugin plugin, String commonPackage) {
            this.instanceProvider = BukkitClassMapperInstanceProvider.defaultProvider();
            this.commonPackage = commonPackage;
            this.mappers = new HashSet<>();
            this.plugin = plugin;
            this.threadPool = Executors.newSingleThreadExecutor();
        }

        public ClassMapperConfiguration build() {
            return new ClassMapperConfiguration(plugin, commonPackage, instanceProvider, mappers, threadPool, waitUntilCompletion,
                    onWrongPermissions, onCommandNotFound, useDebugLogging, reflections, printExceptions);
        }

        public ClassMapperConfigurationBuilder reflections(Reflections reflections) {
            this.reflections = reflections;
            return this;
        }

        public ClassMapperConfigurationBuilder useDebugLogging() {
            this.useDebugLogging = true;
            return this;
        }

        public ClassMapperConfigurationBuilder threadPool(Executor executor) {
            this.threadPool = executor;
            return this;
        }

        public ClassMapperConfigurationBuilder waitUntilCompletion() {
            this.waitUntilCompletion = true;
            return this;
        }

        public ClassMapperConfigurationBuilder commandMapper (String onWrongPermissions, String onCommandNotFound) {
            this.onWrongPermissions = onWrongPermissions;
            this.onCommandNotFound = onCommandNotFound;

            this.mappers.add(CommandMapper.class);

            return this;
        }

        public ClassMapperConfigurationBuilder mobMapper () {
            this.mappers.add(MobMapper.class);
            return this;
        }

        public ClassMapperConfigurationBuilder eventListenerMapper () {
            this.mappers.add(EventListenerMapper.class);
            return this;
        }

        public ClassMapperConfigurationBuilder taskMapper () {
            this.mappers.add(TaskMapper.class);
            return this;
        }

        public ClassMapperConfigurationBuilder printExceptions(boolean printExceptions) {
            this.printExceptions = printExceptions;
            return this;
        }

        public ClassMapperConfigurationBuilder instanceProvider(BukkitClassMapperInstanceProvider instanceProvider) {
            this.instanceProvider = instanceProvider;
            return this;
        }
    }
}
