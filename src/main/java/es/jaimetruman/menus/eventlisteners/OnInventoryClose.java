package es.jaimetruman.menus.eventlisteners;

import es.jaimetruman.menus.InstanceProvider;
import es.jaimetruman.menus.Menu;
import es.jaimetruman.menus.OpenMenuRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.function.Consumer;

public class OnInventoryClose implements Listener {
    private final OpenMenuRepository openMenuRepository;

    public OnInventoryClose() {
        this.openMenuRepository = InstanceProvider.OPEN_MENUS_REPOSITORY;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        this.openMenuRepository.findByPlayerName(event.getPlayer().getName()).ifPresent(menu -> {
            this.executeRegisteredMenuEventListener(event, menu);

            this.openMenuRepository.deleteByPlayerName(event.getPlayer().getName());
        });
    }

    private void executeRegisteredMenuEventListener(InventoryCloseEvent event, Menu menu){
        Consumer<InventoryCloseEvent> onCloseEventListener = menu.configuration().getOnCloseEventListener();

        if(onCloseEventListener != null)
            onCloseEventListener.accept(event);
    }
}
