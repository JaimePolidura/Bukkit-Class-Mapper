package es.jaimetruman.menus;

import lombok.Getter;
import org.bukkit.event.inventory.InventoryType;
import sun.security.rsa.RSAUtil;

import java.util.Arrays;

public enum SupportedInventoryType {
    HOPPER(InventoryType.HOPPER, 1, 5);

    @Getter private final InventoryType bukkitInventoryType;
    @Getter private final int rows;
    @Getter private final int columns;
    @Getter private final int size;

    SupportedInventoryType(InventoryType type, int columns, int rows) {
        this.bukkitInventoryType = type;
        this.rows = rows;
        this.columns = columns;
        this.size = columns * rows;
    }

    public static SupportedInventoryType getByArray(int[][] array){
        int columns = array.length;
        int rows = array[0].length;
        
        return Arrays.stream(SupportedInventoryType.values())
                .filter(inventoryType-> inventoryType.columns == columns && inventoryType.rows == rows)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Inventory type not found"));
    }

    public static int getRowBySlotAndInventoryType(int slot, InventoryType inventoryType){
        int rows = SupportedInventoryType.valueOf(inventoryType.toString()).getRows();

        return slot / rows;
    }

    public static int getColumnBySlotAndInventoryType(int slot, InventoryType inventoryType){
        int cols = SupportedInventoryType.valueOf(inventoryType.toString()).getColumns();
        int actualRow = getRowBySlotAndInventoryType(slot, inventoryType);

        return slot - (actualRow * cols);
    }
}
