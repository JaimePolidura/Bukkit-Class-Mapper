package es.jaimetruman.menus;

import es.jaimetruman._shared.utils.ClassMapperInstanceProvider;
import es.jaimetruman.menus.menubuilder.MenuBuilderService;
import es.jaimetruman.menus.menustate.AfterShow;
import es.jaimetruman.menus.menustate.BeforeShow;
import es.jaimetruman.menus.repository.OpenMenuRepository;
import es.jaimetruman.menus.repository.StaticMenuRepository;
import org.bukkit.entity.Player;

import java.util.List;

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
        callBeforeShow(menu);

        menu.addPages(getPagesForMenu(menu));

        player.openInventory(menu.getInventory());

        this.openMenuRepository.save(player.getName(), menu);
        if(menu.configuration().isStaticMenu()) this.staticMenuRepository.save(menu);

        callAfterShow(menu);
    }

    private List<Page> getPagesForMenu(Menu menu) {
        return menu.configuration().isStaticMenu() ?
                this.staticMenuRepository.findByMenuClass(menu.getClass()).orElse(buildMenuPages(menu)) :
                buildMenuPages(menu);
    }

    private List<Page> buildMenuPages(Menu menu) {
        return this.menuBuilder.build(menu).getPages() ;
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

    public void goBackward(Player player, Menu menu){
        Page page = menu.backward();
        player.openInventory(page.getInventory());
        this.openMenuRepository.save(player.getName(), menu);
    }

    public void goForward(Player player, Menu menu){
        Page page = menu.forward();
        player.openInventory(page.getInventory());
        this.openMenuRepository.save(player.getName(), menu);
    }
}
