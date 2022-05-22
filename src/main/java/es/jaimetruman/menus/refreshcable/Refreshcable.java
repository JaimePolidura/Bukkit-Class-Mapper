package es.jaimetruman.menus.refreshcable;

import es.jaimetruman._shared.utils.ClassMapperInstanceProvider;
import es.jaimetruman.menus.MenuService;
import org.bukkit.inventory.ItemStack;

public interface Refreshcable {
    default void refreshAdd(ItemStack item){
    }
}
