package es.jaimetruman.mobs;

import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * To create a mob you should annotate your class with @Mob and
 * implement this interface. When the mob is clicked, the code in the method
 * will be executed
 */
public interface MobOnInteract {
    void execute(PlayerInteractEntityEvent event);
}
