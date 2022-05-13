package es.jaimetruman.menus.eventlisteners;

import es.jaimetruman.menus.InstanceProvider;
import es.jaimetruman.menus.InventoryTypeService;
import es.jaimetruman.menus.OpenMenuRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.function.Consumer;

public class OnInventoryClick implements Listener {
    private final OpenMenuRepository openMenuRepository;
    private final InventoryTypeService inventoryTypeService;

    public OnInventoryClick() {
        this.openMenuRepository = InstanceProvider.OPEN_MENUS_REPOSITORY;
        this.inventoryTypeService = InstanceProvider.INVENTORY_TYPE_SERVICE;
    }

    @EventHandler
    public void on(InventoryClickEvent event){
        if (event.getView() == null || event.getCurrentItem() == null) return;

        String playerName = event.getWhoClicked().getName();

        this.openMenuRepository.findByPlayerName(playerName).ifPresent(menu -> {
            if(menu.configuration().isFixedItems())
                event.setCancelled(true);

            InventoryType inventoryType = event.getClickedInventory().getType();
            int row = this.inventoryTypeService.getRowBySlotAndInventoryType(event.getSlot(), inventoryType);
            int colmn = this.inventoryTypeService.getColumnBySlotAndInventoryType(event.getSlot(), inventoryType);

            Consumer<InventoryClickEvent> eventConsumer = menu.configuration().getOnClickEventListeners()
                    .get(menu.items()[row][colmn]);

            if (eventConsumer != null){
                eventConsumer.accept(event);
            }
        });
    }
}
