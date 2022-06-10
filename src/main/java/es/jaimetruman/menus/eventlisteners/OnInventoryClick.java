package es.jaimetruman.menus.eventlisteners;

import es.jaimetruman._shared.utils.ClassMapperInstanceProvider;
import es.jaimetruman.menus.*;
import es.jaimetruman.menus.modules.numberselector.NumberSelectActionType;
import es.jaimetruman.menus.modules.numberselector.NumberSelectorControllItem;
import es.jaimetruman.menus.modules.numberselector.NumberSelectorMenuConfiguration;
import es.jaimetruman.menus.repository.OpenMenuRepository;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.function.BiConsumer;

import static org.bukkit.ChatColor.*;

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
            try{
                tryPerformClickOnMenu(event, menu);
            }catch (Exception e) {
                event.getWhoClicked().sendMessage(DARK_RED + "Some error happened " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void tryPerformClickOnMenu(InventoryClickEvent event, Menu menu) {
        InventoryType inventoryType = event.getClickedInventory().getType();
        int row = SupportedInventoryType.getRowBySlot(event.getSlot(), inventoryType);
        int column = SupportedInventoryType.getColumnBySlot(event.getSlot(), inventoryType);
        int itemNumClicked = menu.items()[row][column];

        if(menu.getConfiguration().isFixedItems())
            event.setCancelled(true);

        boolean inventorTypePlayer = event.getView() == null || event.getCurrentItem() == null ||
                event.getClickedInventory().getType() == InventoryType.PLAYER;

        if (!inventorTypePlayer){
            performOnClickInMenu(event, menu, row, column, itemNumClicked);
        }
    }

    private void performOnClickInMenu(InventoryClickEvent event, Menu menu, int row, int column, int itemNumClicked) {
        BiConsumer<Player, InventoryClickEvent> eventConsumer = menu.getConfiguration().getOnClickEventListeners()
                .get(menu.getActualItemNums()[row][column]);

        if (eventConsumer != null){
            tryToExcuteOnClick(event, eventConsumer);
        }

        OnMenuClickedListeners.notify((Player) event.getWhoClicked(), menu, itemNumClicked);
    }

    private void tryToExcuteOnClick(InventoryClickEvent event, BiConsumer<Player, InventoryClickEvent> eventConsumer) {
        try{
            eventConsumer.accept((Player) event.getWhoClicked(), event);
        }catch (Exception e) {
            event.getWhoClicked().sendMessage(DARK_RED + e.getMessage());
            this.menuService.close((Player) event.getWhoClicked());
        }
    }
}
