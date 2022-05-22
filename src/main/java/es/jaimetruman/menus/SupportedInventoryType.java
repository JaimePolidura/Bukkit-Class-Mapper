package es.jaimetruman.menus;

import lombok.Getter;
import org.bukkit.event.inventory.InventoryType;

import java.util.Arrays;

public enum SupportedInventoryType {
    HOPPER(InventoryType.HOPPER, 1, 5),
    CHEST(InventoryType.CHEST, 3, 9),
    BIG_CHEST(InventoryType.CHEST, 6, 9);

    @Getter private final InventoryType bukkitInventoryType;
    @Getter private final int rows;
    @Getter private final int columns;
    @Getter private final int size;

    SupportedInventoryType(InventoryType type, int rows, int columns) {
        this.bukkitInventoryType = type;
        this.rows = rows;
        this.columns = columns;
        this.size = columns * rows;
    }

    public static SupportedInventoryType getByArray(int[][] array){
        int columns = array[0].length;
        int rows = array.length;

        return Arrays.stream(SupportedInventoryType.values())
                .filter(inventoryType-> inventoryType.columns == columns && inventoryType.rows == rows)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Inventory type not found"));
    }

    public static int getRowBySlot(int slot, InventoryType inventoryType){
        int columns = SupportedInventoryType.valueOf(inventoryType.toString()).getColumns();

        return (slot) / columns;
    }

    public static int getColumnBySlot(int slot, InventoryType inventoryType){
        int cols = SupportedInventoryType.valueOf(inventoryType.toString()).getColumns();
        int actualRow = getRowBySlot(slot, inventoryType);

        return (slot) - (cols * actualRow);
    }
}
