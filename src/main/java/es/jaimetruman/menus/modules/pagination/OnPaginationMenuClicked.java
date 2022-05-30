package es.jaimetruman.menus.modules.pagination;

import es.jaimetruman._shared.utils.ClassMapperInstanceProvider;
import es.jaimetruman.menus.Menu;
import es.jaimetruman.menus.OnMenuClicked;
import es.jaimetruman.menus.Page;
import es.jaimetruman.menus.repository.OpenMenuRepository;
import org.bukkit.entity.Player;

public final class OnPaginationMenuClicked implements OnMenuClicked {
    private final OpenMenuRepository openMenuRepository;

    public OnPaginationMenuClicked() {
        this.openMenuRepository = ClassMapperInstanceProvider.OPEN_MENUS_REPOSITORY;
    }

    @Override
    public void on(Player player, Menu menu, int itemNumClicked) {
        if(hasClickedPaginationsItems(menu, itemNumClicked)){
            performPaginationAction(player, menu, itemNumClicked);
        }
    }

    private void performPaginationAction(Player player, Menu menu, int itemNumClicked) {
        if(itemNumClicked == menu.configuration().getPaginationConfiguration().getBackward().getItemNum())
            goBackward(player, menu);
        else
            goForward(player, menu);
    }

    private void goForward(Player player, Menu menu) {
        Page page = menu.forward();
        player.openInventory(page.getInventory());
        this.openMenuRepository.save(player.getName(), menu);
    }

    private void goBackward(Player player, Menu menu) {
        Page page = menu.backward();
        player.openInventory(page.getInventory());
        this.openMenuRepository.save(player.getName(), menu);
    }

    private boolean hasClickedPaginationsItems(Menu menu, int itemNumClicked) {
        return menu.configuration().isPaginated() && (menu.configuration().getPaginationConfiguration().getBackward().getItemNum() == itemNumClicked ||
                menu.configuration().getPaginationConfiguration().getForward().getItemNum() == itemNumClicked);
    }

}
