package es.bukkitclassmapper.mobs;

import es.bukkitclassmapper.ClassMapperConfiguration;
import es.bukkitclassmapper.ClassMapper;
import es.bukkitclassmapper._shared.utils.ClassMapperLogger;
import es.bukkitclassmapper._shared.utils.reflections.BukkitClassMapperInstanceProvider;
import es.jaime.javaddd.domain.exceptions.ResourceNotFound;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.*;
import java.util.stream.Stream;

public final class MobMapper extends ClassMapper {
    private final ClassMapperConfiguration configuartion;
    private final Map<Location, MobInfo> mappedMobs;

    public MobMapper(ClassMapperConfiguration configuration, ClassMapperLogger logger) {
        super(configuration, logger);

        this.configuartion = configuration;
        this.mappedMobs = new HashMap<>();
        DefaultEntrypointPlayerInteractEntity defaultListener = new DefaultEntrypointPlayerInteractEntity();

        this.configuartion.getPlugin()
                .getServer()
                .getPluginManager()
                .registerEvents(defaultListener, this.configuartion.getPlugin());
    }

    @Override
    public void scan() {
        Set<Class<? extends OnPlayerInteractMob>> checkedClasses = this.checkIfClassesImplementsMobInterface(
                reflections.getTypesAnnotatedWith(Mob.class));

        this.createInstancesAndAdd(checkedClasses);
    }

    private Set<Class<? extends OnPlayerInteractMob>> checkIfClassesImplementsMobInterface(Set<Class<?>> classes) {
        Set<Class<? extends OnPlayerInteractMob>> checkedClasses = new HashSet<>();

        for(Class<?> notCheckedClass : classes){
            if(OnPlayerInteractMob.class.isAssignableFrom(notCheckedClass)){
                checkedClasses.add((Class<? extends OnPlayerInteractMob>) notCheckedClass);
            }else{
                logger.error("Couldn't initialize mob in class %s. This class should implement MobOnInteract interface", notCheckedClass.getName());
            }
        }

        return checkedClasses;
    }

    private void createInstancesAndAdd(Set<Class<? extends OnPlayerInteractMob>> classes) {
        for(Class<? extends OnPlayerInteractMob> classToAdd : classes){
            Mob annotation = this.getMobExecutorAnnotationFromClass(classToAdd);

            saveMobClassInstance(classToAdd, annotation, this.configuartion.getInstanceProvider());
        }

        logger.info("Mapped all mob classes. Total: %s", classes.size());
    }

    private Mob getMobExecutorAnnotationFromClass(Class<? extends OnPlayerInteractMob> classToFind) {
        return (Mob) Stream.of(classToFind.getAnnotations())
                .filter(annotation -> annotation instanceof Mob)
                .findAny()
                .get();
    }

    @SneakyThrows
    private void saveMobClassInstance(Class<? extends OnPlayerInteractMob> mobClass, Mob mobMeta,
                                      BukkitClassMapperInstanceProvider instanceProvider) {
        if(configuartion.getInstanceProvider().isExcluded(mobClass)){
            return;
        }
        OnPlayerInteractMob mobClassInstance = instanceProvider.get(mobClass);
        if(mobClassInstance == null){
            throw new ResourceNotFound(String.format("Bukkit OnPlayerInteractMob %s provided by dependency provider is null", mobClass));
        }

        int x = mobMeta.x();
        int y = mobMeta.y();
        int z = mobMeta.z();

        Location mobLocation = new Location(null, x, y, z);

        this.mappedMobs.put(mobLocation, new MobInfo(mobClassInstance, mobMeta));

        logger.debug("Registered mob class %s", mobClass.getName());
    }

    private Optional<MobInfo> findByCords(Location location) {
        return Optional.ofNullable(this.mappedMobs.get(location));
    }

    public final class DefaultEntrypointPlayerInteractEntity implements Listener {
        @EventHandler
        public void on (PlayerInteractEntityEvent event) {
            EquipmentSlot tipoClick = event.getHand();

            if(tipoClick.equals(EquipmentSlot.HAND)){
                searchAndExecuteMob(event);
            }
        }

        private void searchAndExecuteMob (PlayerInteractEntityEvent event) {
            Location location = transformLocationToWorldNull(event.getRightClicked().getLocation());
            Optional<MobInfo> mobOptional = findByCords(location);

            mobOptional.ifPresent(mobInfo -> configuartion.getThreadPool().execute(() -> {
                mobInfo.getListener().execute(event);
            }));
        }

        // Needed to perform a search in the hashmap because we don't sabe world objects
        private Location transformLocationToWorldNull (Location location) {
            return new Location(null, (int) location.getX(), (int) location.getY(), (int) location.getZ());
        }
    }

    @AllArgsConstructor
    private static final class MobInfo {
        @Getter private final OnPlayerInteractMob listener;
        @Getter private final Mob mobData;
    }
}
