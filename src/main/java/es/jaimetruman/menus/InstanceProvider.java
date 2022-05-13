package es.jaimetruman.menus;

public class InstanceProvider {
    public static OpenMenuRepository OPEN_MENUS_REPOSITORY = new OpenMenuRepository();
    public static MenuService MENU_SERVICE = new MenuService();
    public static InventoryTypeService INVENTORY_TYPE_SERVICE = new InventoryTypeService();
}
