package es.jaimetruman.mobs;

import javafx.util.Pair;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.*;
import java.util.stream.Stream;

/**
 * This maps mobs names with instances of their respectieve mob class,
 * which has to be annotatd with @Mob and implement MobOnInteract
 */
public final class MobMapper {
    private final Map<Location, Pair<OnPlayerInteractMob, Mob>> mappedMobs;
    private final Plugin mainPluginClass;
    private final Reflections reflections;
    private final DefaultEntrypointPlayerInteractEntity defaultListener;
    private final String packageToStartScanning;

    /**
     *
     * @param packageToStartScanning Base package where your will be scanned for classes who represents mobs.
     * @param plugin Main plugin class (the one that extends javaplugin)
     * Example:
     * MobMapper.create("es.jaimetruman.mobs", MainPluginClass.getInstance());
     */
    public static MobMapper create (String packageToStartScanning, Plugin plugin) {
        return new MobMapper(packageToStartScanning, plugin);
    }

    private MobMapper(String packageToStartScanning, Plugin plugin) {
        this.mappedMobs = new HashMap<>();
        this.defaultListener = new DefaultEntrypointPlayerInteractEntity();
        this.mainPluginClass = plugin;

        this.packageToStartScanning = packageToStartScanning;

        this.reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packageToStartScanning))
                .setScanners(new TypeAnnotationsScanner(),
                             new SubTypesScanner()));

        this.scanFormMobClasses();

        plugin.getServer().getPluginManager().registerEvents(defaultListener, plugin);
    }

    private void scanFormMobClasses() {
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
