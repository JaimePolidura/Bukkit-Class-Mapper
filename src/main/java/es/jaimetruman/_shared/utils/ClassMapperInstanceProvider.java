package es.jaimetruman._shared.utils;

import es.jaimetruman.menus.MenuService;
import es.jaimetruman.menus.refreshcable.RefreshcableMenuService;
import es.jaimetruman.menus.repository.OpenMenuRepository;
import es.jaimetruman.menus.repository.StaticMenuRepository;

public class ClassMapperInstanceProvider {
    public static OpenMenuRepository OPEN_MENUS_REPOSITORY = new OpenMenuRepository();
    public static StaticMenuRepository STATIC_MENUS_REPOSITORY = new StaticMenuRepository();
    public static MenuService MENU_SERVICE = new MenuService();
    public static RefreshcableMenuService REFRESHCABLE_MENU_SERVICE = new RefreshcableMenuService();
}
