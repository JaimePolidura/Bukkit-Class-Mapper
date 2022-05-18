package es.jaimetruman.menus;

import es.jaimetruman.menus.inventorybuilder.MenuInventory;
import es.jaimetruman.menus.inventorybuilder.MenuInventoryBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MenuService {
    private final MenuInventoryBuilder inventoryBuilder;
    private final OpenMenuRepository openMenuRepository;

    public MenuService() {
        this.openMenuRepository = InstanceProvider.OPEN_MENUS_REPOSITORY;
        this.inventoryBuilder = new MenuInventoryBuilder();
    }

    public void open(Player player, Menu menu){
        MenuInventory inventory = this.inventoryBuilder.build(menu);
        player.openInventory(inventory.getInventory());

        this.openMenuRepository.save(player.getName(), menu);
    }

    public void close(Player player){
        this.openMenuRepository.findByPlayerName(player.getName()).ifPresent(menu -> {
            player.closeInventory();
        });
    }
}
