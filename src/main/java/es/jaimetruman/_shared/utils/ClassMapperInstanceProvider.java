package es.jaimetruman._shared.utils;

import es.jaimetruman.menus.MenuService;
import es.jaimetruman.menus.menubuilder.MenuBuilderService;
import es.jaimetruman.menus.modules.messaging.MessagingMenuService;
import es.jaimetruman.menus.modules.numberselector.NumberSelectorService;
import es.jaimetruman.menus.modules.pagination.PaginationService;
import es.jaimetruman.menus.repository.OpenMenuRepository;
import es.jaimetruman.menus.repository.StaticMenuRepository;

public class ClassMapperInstanceProvider {
    public static final OpenMenuRepository OPEN_MENUS_REPOSITORY = new OpenMenuRepository();
    public static final StaticMenuRepository STATIC_MENUS_REPOSITORY = new StaticMenuRepository();
    public static final MenuService MENU_SERVICE = new MenuService();
    public static final MessagingMenuService REFRESHCABLE_MENU_SERVICE = new MessagingMenuService();
    public static final NumberSelectorService NUMBER_SELECTOR_SERVICE = new NumberSelectorService();
    public static final PaginationService PAGINATION_SERVICE = new PaginationService();
    public static final MenuBuilderService MENU_BUILDER_SERVICE = new MenuBuilderService();
}
