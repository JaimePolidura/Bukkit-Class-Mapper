package es.jaimetruman.menus.eventlisteners;

import es.jaimetruman._shared.utils.ClassMapperInstanceProvider;
import es.jaimetruman.menus.*;
import es.jaimetruman.menus.repository.OpenMenuRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.function.BiConsumer;

public class OnInventoryClick implements Listener {
    private final OpenMenuRepository openMenuRepository;
    private final MenuService menuService;

    public OnInventoryClick() {
        this.openMenuRepository = ClassMapperInstanceProvider.OPEN_MENUS_REPOSITORY;
        this.menuService = ClassMapperInstanceProvider.MENU_SERVICE;
    }

    @EventHandler
    public void on(InventoryClickEvent event){
        String playerName = event.getWhoClicked().getName();

        if(event.getClickedInventory() == null) return;

        this.openMenuRepository.findByPlayerName(playerName).ifPresent(menu -> {
            InventoryType inventoryType = event.getClickedInventory().getType();
            int row = SupportedInventoryType.getRowBySlot(event.getSlot(), inventoryType);
            int column = SupportedInventoryType.getColumnBySlot(event.getSlot(), inventoryType);
            int itemNumClicked = menu.items()[row][column];

            if(menu.configuration().isFixedItems())
                event.setCancelled(true);

            if (event.getView() == null || event.getCurrentItem() == null ||
                    event.getClickedInventory().getType() == InventoryType.PLAYER) return;

            BiConsumer<Player, InventoryClickEvent> eventConsumer = menu.configuration().getOnClickEventListeners()
                    .get(menu.getItemsNums()[row][column]);

            if (eventConsumer != null)
                eventConsumer.accept((Player) event.getWhoClicked(), event);

            if (hasClickedPaginationsItems(menu, itemNumClicked))
                performPaginationControlledClicked(menu, itemNumClicked, event);

            if(hasClickedConfirmationItems(menu, itemNumClicked))
                performConfirmationAction(menu, itemNumClicked, event);
        });
    }

    private boolean hasClickedConfirmationItems(Menu menu, int itemNumClicked){
        return menu.configuration().isConfirmation() && (menu.configuration().getConfirmationConfiguration().getCancel().getItemNum() == itemNumClicked ||
                menu.configuration().getConfirmationConfiguration().getAccept().getItemNum() == itemNumClicked);
    }

    private boolean hasClickedPaginationsItems(Menu menu, int itemNumClicked) {
        return menu.configuration().isPaginated() && (menu.configuration().getPaginationConfiguration().getBackward().getItemNum() == itemNumClicked ||
                menu.configuration().getPaginationConfiguration().getForward().getItemNum() == itemNumClicked);
    }

    private void performPaginationControlledClicked(Menu menu, int itemNumClicked, InventoryClickEvent event) {
        if(itemNumClicked == menu.configuration().getPaginationConfiguration().getBackward().getItemNum())
            this.menuService.goBackward((Player) event.getWhoClicked(), menu);
        else
            this.menuService.goForward((Player) event.getWhoClicked(), menu);
    }

    private void performConfirmationAction(Menu menu, int itemNumClicked, InventoryClickEvent event) {
        if(menu.configuration().getConfirmationConfiguration().isCloseOnAction())
            this.menuService.close((Player) event.getWhoClicked());
    }
}
