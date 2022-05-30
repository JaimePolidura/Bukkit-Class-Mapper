package es.jaimetruman.menus.eventlisteners;

import es.jaimetruman._shared.utils.ClassMapperInstanceProvider;
import es.jaimetruman.menus.*;
import es.jaimetruman.menus.modules.numberselector.NumberSelectActionType;
import es.jaimetruman.menus.modules.numberselector.NumberSelectorControllItem;
import es.jaimetruman.menus.modules.numberselector.NumberSelectorMenuConfiguration;
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
                    .get(menu.getActualItemNums()[row][column]);

            if (eventConsumer != null)
                eventConsumer.accept((Player) event.getWhoClicked(), event);

            OnMenuClickedListeners.notify((Player) event.getWhoClicked(), menu, itemNumClicked);
        });
    }
}
