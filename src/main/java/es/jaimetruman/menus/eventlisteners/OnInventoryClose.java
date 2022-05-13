package es.jaimetruman.menus.eventlisteners;

import es.jaimetruman.menus.InstanceProvider;
import es.jaimetruman.menus.OpenMenuRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnInventoryClose implements Listener {
    private final OpenMenuRepository openMenuRepository;

    public OnInventoryClose() {
        this.openMenuRepository = InstanceProvider.OPEN_MENUS_REPOSITORY;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        this.openMenuRepository.deleteByPlayerName(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        this.openMenuRepository.deleteByPlayerName(event.getPlayer().getName());
    }
}
