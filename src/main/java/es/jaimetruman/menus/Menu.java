package es.jaimetruman.menus;

import org.bukkit.event.inventory.InventoryType;

public abstract class Menu {
    public Menu(){
    }

    public abstract int[][] items();
    public abstract MenuConfiguration configuration();
}
