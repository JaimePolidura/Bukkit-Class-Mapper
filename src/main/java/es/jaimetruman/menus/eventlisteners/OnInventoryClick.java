package es.jaimetruman.menus.eventlisteners;

import es.jaimetruman._shared.utils.ClassMapperInstanceProvider;
import es.jaimetruman.menus.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class OnInventoryClick implements Listener {
    private final OpenMenuRepository openMenuRepository;
    private final MenuService menuService;

    public OnInventoryClick() {
        this.openMenuRepository = ClassMapperInstanceProvider.OPEN_MENUS_REPOSITORY;
        this.menuService = ClassMapperInstanceProvider.MENU_SERVICE;
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

            BiConsumer<Player, InventoryClickEvent> eventConsumer = menu.configuration().getOnClickEventListeners()
                    .get(menu.getItemsNums()[row][column]);

            if (eventConsumer != null)
                eventConsumer.accept((Player) event.getWhoClicked(), event);

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
            this.menuService.goBackward((Player) event.getWhoClicked(), menu);
        else
            this.menuService.goForward((Player) event.getWhoClicked(), menu);
    }
}
