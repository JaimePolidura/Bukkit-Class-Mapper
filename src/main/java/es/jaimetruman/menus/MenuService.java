package es.jaimetruman.menus;

import es.jaimetruman.menus.menubuilder.MenuBuildResult;
import es.jaimetruman.menus.menubuilder.MenuBuilderService;
import org.bukkit.entity.Player;

public class MenuService {
    private final MenuBuilderService inventoryBuilder;
    private final OpenMenuRepository openMenuRepository;

    public MenuService() {
        this.openMenuRepository = InstanceProvider.OPEN_MENUS_REPOSITORY;
        this.inventoryBuilder = new MenuBuilderService();
    }

    public void open(Player player, Menu menu){
        MenuBuildResult menuBuildResult = this.inventoryBuilder.build(menu);
        menu.addAll(menuBuildResult.getPages());

        player.openInventory(menu.getActualPageInventory());

        this.openMenuRepository.save(player.getName(), menu);
    }

    public void close(Player player){
        this.openMenuRepository.findByPlayerName(player.getName()).ifPresent(menu -> {
            player.closeInventory();
        });
    }
}
