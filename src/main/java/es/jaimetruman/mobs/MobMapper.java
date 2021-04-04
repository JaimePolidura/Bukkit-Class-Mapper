package es.jaimetruman.mobs;

import es.jaimetruman.ClassScanner;
import javafx.util.Pair;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Stream;


public final class MobMapper extends ClassScanner {
    private final Map<Location, Pair<OnPlayerInteractMob, Mob>> mappedMobs;
    private final Plugin mainPluginClass;
    private final DefaultEntrypointPlayerInteractEntity defaultListener;

    public MobMapper(String packageToStartScanning, Plugin plugin) {
        super(packageToStartScanning);

        this.mappedMobs = new HashMap<>();
        this.defaultListener = new DefaultEntrypointPlayerInteractEntity();
        this.mainPluginClass = plugin;

        this.scan();

        plugin.getServer().getPluginManager().registerEvents(defaultListener, plugin);
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

    private void createInstancesAndAdd (Set<Class<? extends OnPlayerInteractMob>> classes) {
        for(Class<? extends OnPlayerInteractMob> classToAdd : classes){
            Mob annotation = this.getMobExecutorAnnotationFromClass(classToAdd);

            saveMobClassInstance(classToAdd, annotation);
        }
    }

    private Mob getMobExecutorAnnotationFromClass(Class<? extends OnPlayerInteractMob> classToFind) {
        return (Mob) Stream.of(classToFind.getAnnotations())
                .filter(annotation -> annotation instanceof Mob)
                .findAny()
                .get();
    }

    @SneakyThrows
    private void saveMobClassInstance(Class<? extends OnPlayerInteractMob> mobClass, Mob mobMeta) {
        OnPlayerInteractMob mobClassInstance = mobClass.newInstance();

        int x = mobMeta.x();
        int y = mobMeta.y();
        int z = mobMeta.z();

        Location mobLocation = new Location(null, x, y, z);

        this.mappedMobs.put(mobLocation, new Pair<>(mobClassInstance, mobMeta));
    }

    private Optional<Pair<OnPlayerInteractMob, Mob>> findByCords(Location location) {
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
            Optional<Pair<OnPlayerInteractMob, Mob>> mobOptional = findByCords(location);

            if(mobOptional.isPresent()){
                mobOptional.get().getKey().execute(event);
            }
        }

        // Needed to perform a search in the hashmap because we dont sabe world objects
        private Location transformLocationToWorldNull (Location location) {
            return new Location(null, (int) location.getX(), (int) location.getY(), (int) location.getZ());
        }
    }
}
