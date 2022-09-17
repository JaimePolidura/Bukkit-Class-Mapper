package es.bukkitclassmapper.mobs;

import es.bukkitclassmapper.ClassMapperConfiguration;
import es.bukkitclassmapper.ClassMapper;
import es.bukkitclassmapper._shared.utils.reflections.InstanceProvider;
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
    private final DefaultEntrypointPlayerInteractEntity defaultListener;
    private final Map<Location, MobInfo> mappedMobs;

    public MobMapper(ClassMapperConfiguration configuration) {
        super(configuration);

        this.configuartion = configuration;
        this.mappedMobs = new HashMap<>();
        this.defaultListener = new DefaultEntrypointPlayerInteractEntity();

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
                System.out.println("Couldn't initialize mob in class " + notCheckedClass + ". This class should implement MobOnInteract interface");
            }
        }

        return checkedClasses;
    }

    private void createInstancesAndAdd(Set<Class<? extends OnPlayerInteractMob>> classes) {
        for(Class<? extends OnPlayerInteractMob> classToAdd : classes){
            Mob annotation = this.getMobExecutorAnnotationFromClass(classToAdd);

            saveMobClassInstance(classToAdd, annotation, this.configuartion.getInstanceProvider());
        }

        System.out.println("Mapped all mob classes");
    }

    private Mob getMobExecutorAnnotationFromClass(Class<? extends OnPlayerInteractMob> classToFind) {
        return (Mob) Stream.of(classToFind.getAnnotations())
                .filter(annotation -> annotation instanceof Mob)
                .findAny()
                .get();
    }

    @SneakyThrows
    private void saveMobClassInstance(Class<? extends OnPlayerInteractMob> mobClass, Mob mobMeta,
                                      InstanceProvider instanceProvider) {
        OnPlayerInteractMob mobClassInstance = instanceProvider.get(mobClass);

        int x = mobMeta.x();
        int y = mobMeta.y();
        int z = mobMeta.z();

        Location mobLocation = new Location(null, x, y, z);

        this.mappedMobs.put(mobLocation, new MobInfo(mobClassInstance, mobMeta));
    }

    private Optional<MobInfo> findByCords(Location location) {
        return Optional.ofNullable(this.mappedMobs.get(location));
    }

    private final class DefaultEntrypointPlayerInteractEntity implements Listener {
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

            mobOptional.ifPresent(mobInfo -> configuartion.getCommonThreadPool().execute(() -> {
                mobInfo.getListener().execute(event);
            }));
        }

        // Needed to perform a search in the hashmap because we don't sabe world objects
        private Location transformLocationToWorldNull (Location location) {
            return new Location(null, (int) location.getX(), (int) location.getY(), (int) location.getZ());
        }
    }

    @AllArgsConstructor
    private static final class MobInfo{
        @Getter private final OnPlayerInteractMob listener;
        @Getter private final Mob mobData;
    }
}
