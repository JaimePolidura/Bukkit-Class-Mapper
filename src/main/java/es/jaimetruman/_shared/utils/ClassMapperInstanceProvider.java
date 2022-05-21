package es.jaimetruman._shared.utils;

import es.jaimetruman.menus.MenuService;
import es.jaimetruman.menus.OpenMenuRepository;

public class ClassMapperInstanceProvider {
    public static OpenMenuRepository OPEN_MENUS_REPOSITORY = new OpenMenuRepository();
    public static MenuService MENU_SERVICE = new MenuService();
}
