package es.bukkitclassmapper.menus.modules.pagination;

import es.bukkitclassmapper._shared.utils.reflections.ClassMapperInstanceProvider;
import es.bukkitclassmapper.menus.Menu;
import es.bukkitclassmapper.menus.Page;
import es.bukkitclassmapper.menus.repository.OpenMenuRepository;
import org.bukkit.entity.Player;

public final class PaginationService {
    private final OpenMenuRepository openMenuRepository;

    public PaginationService() {
        this.openMenuRepository = ClassMapperInstanceProvider.OPEN_MENUS_REPOSITORY;
    }

    public void goForward(Player player, Menu menu) {
        Page page = menu.forward();
        player.openInventory(page.getInventory());
        this.openMenuRepository.save(player.getName(), menu);
    }

    public void goBackward(Player player, Menu menu) {
        Page page = menu.backward();
        player.openInventory(page.getInventory());
        this.openMenuRepository.save(player.getName(), menu);
    }

}
