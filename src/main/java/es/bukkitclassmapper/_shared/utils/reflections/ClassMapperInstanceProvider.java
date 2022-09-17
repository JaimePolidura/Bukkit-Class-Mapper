package es.bukkitclassmapper._shared.utils.reflections;

import es.bukkitclassmapper.menus.MenuService;
import es.bukkitclassmapper.menus.menubuilder.MenuBuilderService;
import es.bukkitclassmapper.menus.modules.messaging.MessagingMenuService;
import es.bukkitclassmapper.menus.modules.numberselector.NumberSelectorService;
import es.bukkitclassmapper.menus.modules.pagination.PaginationService;
import es.bukkitclassmapper.menus.modules.sync.SyncMenuService;
import es.bukkitclassmapper.menus.repository.OpenMenuRepository;
import es.bukkitclassmapper.menus.repository.StaticMenuRepository;

public class ClassMapperInstanceProvider {
    public static final OpenMenuRepository OPEN_MENUS_REPOSITORY = new OpenMenuRepository();
    public static final StaticMenuRepository STATIC_MENUS_REPOSITORY = new StaticMenuRepository();
    public static final MenuService MENU_SERVICE = new MenuService();
    public static final MessagingMenuService REFRESHCABLE_MENU_SERVICE = new MessagingMenuService();
    public static final NumberSelectorService NUMBER_SELECTOR_SERVICE = new NumberSelectorService();
    public static final PaginationService PAGINATION_SERVICE = new PaginationService();
    public static final MenuBuilderService MENU_BUILDER_SERVICE = new MenuBuilderService();
    public static final SyncMenuService SYNC_MENU_SERVICE = new SyncMenuService();
}
