package es.jaimetruman.menus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.inventory.Inventory;

@AllArgsConstructor
@ToString
public final class Page {
    @Getter private final Inventory inventory;
    @Getter private final int[][] itemsNums;
}
