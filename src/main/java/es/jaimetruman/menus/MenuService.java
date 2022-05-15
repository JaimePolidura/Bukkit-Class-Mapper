package es.jaimetruman.menus;

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
        Inventory inventory = this.inventoryBuilder.build(menu);
        player.openInventory(inventory);

        this.openMenuRepository.save(player.getName(), menu);
    }

    public void close(Player player){
        this.openMenuRepository.findByPlayerName(player.getName()).ifPresent(menu -> {
            player.closeInventory();
        });
    }
}
