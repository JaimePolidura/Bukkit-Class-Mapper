package es.bukkitclassmapper.menus;

import es.bukkitclassmapper._shared.utils.ClassMapperInstanceProvider;
import es.bukkitclassmapper.menus.menubuilder.MenuBuilderService;
import es.bukkitclassmapper.menus.menustate.AfterShow;
import es.bukkitclassmapper.menus.menustate.BeforeShow;
import es.bukkitclassmapper.menus.repository.OpenMenuRepository;
import es.bukkitclassmapper.menus.repository.StaticMenuRepository;
import org.bukkit.entity.Player;

import java.util.List;

import static org.bukkit.ChatColor.*;

public class MenuService {
    private final MenuBuilderService menuBuilder;
    private final OpenMenuRepository openMenuRepository;
    private final StaticMenuRepository staticMenuRepository;

    public MenuService() {
        this.openMenuRepository = ClassMapperInstanceProvider.OPEN_MENUS_REPOSITORY;
        this.staticMenuRepository = ClassMapperInstanceProvider.STATIC_MENUS_REPOSITORY;
        this.menuBuilder = new MenuBuilderService();
    }

    public void open(Player player, Menu menu){
        try {
            tryToOpenMenu(player, menu);
        }catch (Exception e) {
            player.sendMessage(DARK_RED + "Some error happened " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void tryToOpenMenu(Player player, Menu menu) {
        callBeforeShow(menu);

        menu.addPages(buildPages(menu));

        player.openInventory(menu.getInventory());

        this.openMenuRepository.save(player.getName(), menu);

        if(menu.getConfiguration().isStaticMenu()) this.staticMenuRepository.save(menu);

        callAfterShow(menu);
    }

    public List<Page> buildPages(Menu menu){
        return menu.getConfiguration().isStaticMenu() ?
                this.staticMenuRepository.findByMenuClass(menu.getClass()).orElse(buildMenuPages(menu)) :
                buildMenuPages(menu);
    }

    private List<Page> buildMenuPages(Menu menu) {
        MenuBuilderService newMenuBuilderService = new MenuBuilderService();

        return newMenuBuilderService.createPages(menu.getConfiguration(), menu.getBaseItemNums());
    }

    private void callAfterShow(Menu menu) {
        if(menu instanceof AfterShow) ((AfterShow) menu).afterShow();
    }

    private void callBeforeShow(Menu menu) {
        if(menu instanceof BeforeShow) ((BeforeShow) menu).beforeShow();
    }

    public void close(Player player){
        this.openMenuRepository.findByPlayerName(player.getName()).ifPresent(menu -> {
            player.closeInventory();
        });
    }
}
