package es.jaimetruman._shared.utils;

import es.jaimetruman.menus.MenuService;
import es.jaimetruman.menus.messaging.MessagingMenuService;
import es.jaimetruman.menus.repository.OpenMenuRepository;
import es.jaimetruman.menus.repository.StaticMenuRepository;

public class ClassMapperInstanceProvider {
    public static OpenMenuRepository OPEN_MENUS_REPOSITORY = new OpenMenuRepository();
    public static StaticMenuRepository STATIC_MENUS_REPOSITORY = new StaticMenuRepository();
    public static MenuService MENU_SERVICE = new MenuService();
    public static MessagingMenuService REFRESHCABLE_MENU_SERVICE = new MessagingMenuService();
}
