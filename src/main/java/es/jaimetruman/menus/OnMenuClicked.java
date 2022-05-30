package es.jaimetruman.menus;

import org.bukkit.entity.Player;

public interface OnMenuClicked {
    void on(Player player, Menu menu, int itemNumClicked);
}
