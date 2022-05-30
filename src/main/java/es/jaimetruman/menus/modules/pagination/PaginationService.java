package es.jaimetruman.menus.modules.pagination;

import es.jaimetruman._shared.utils.ClassMapperInstanceProvider;
import es.jaimetruman.menus.Menu;
import es.jaimetruman.menus.Page;
import es.jaimetruman.menus.repository.OpenMenuRepository;
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
