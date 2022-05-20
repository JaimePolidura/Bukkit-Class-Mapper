package es.jaimetruman.menus.eventlisteners;

import es.jaimetruman.menus.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.function.Consumer;

public class OnInventoryClick implements Listener {
    private final OpenMenuRepository openMenuRepository;

    public OnInventoryClick() {
        this.openMenuRepository = InstanceProvider.OPEN_MENUS_REPOSITORY;
    }

    @EventHandler
    public void on(InventoryClickEvent event){
        if (event.getView() == null || event.getCurrentItem() == null ||
                event.getClickedInventory().getType() == InventoryType.PLAYER) return;

        String playerName = event.getWhoClicked().getName();

        this.openMenuRepository.findByPlayerName(playerName).ifPresent(menu -> {
            InventoryType inventoryType = event.getClickedInventory().getType();
            int row = SupportedInventoryType.getRowBySlotAndInventoryType(event.getSlot(), inventoryType);
            int column = SupportedInventoryType.getColumnBySlotAndInventoryType(event.getSlot(), inventoryType);
            int itemNumClicked = menu.items()[row][column];

            if(menu.configuration().isFixedItems())
                event.setCancelled(true);

            Consumer<InventoryClickEvent> eventConsumer = menu.configuration().getOnClickEventListeners()
                    .get(menu.getItemsNums()[row][column]);

            if (eventConsumer != null)
                eventConsumer.accept(event);

            if (hasClickedPaginationsItems(menu, itemNumClicked))
                performPaginationControlledClicked(menu, itemNumClicked, event);
        });
    }

    private boolean hasClickedPaginationsItems(Menu menu, int itemNumClicked) {
        return menu.configuration().isPaginated() && (menu.configuration().getMenuPaginationConfiguration().getBackward().getItemNum() == itemNumClicked ||
                menu.configuration().getMenuPaginationConfiguration().getForward().getItemNum() == itemNumClicked);
    }

    private void performPaginationControlledClicked(Menu menu, int itemNumClicked, InventoryClickEvent event) {
        if(itemNumClicked == menu.configuration().getMenuPaginationConfiguration().getBackward().getItemNum())
            this.goBackward(menu, event);
        else
            this.goForward(menu, event);
    }

    private void goForward(Menu menu, InventoryClickEvent event) {
        Page page = menu.forward();
        event.getWhoClicked().openInventory(page.getInventory());
    }

    private void goBackward(Menu menu, InventoryClickEvent event) {
        Page page = menu.backward();
        event.getWhoClicked().openInventory(page.getInventory());
    }
}
