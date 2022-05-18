package es.jaimetruman.menus.inventorybuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
public final class MenuInventory {
    @Getter private final Inventory inventory;
    @Getter private final List<ItemStack> overflowItems;
}
