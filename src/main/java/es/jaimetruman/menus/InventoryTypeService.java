package es.jaimetruman.menus;

import lombok.Getter;
import org.bukkit.event.inventory.InventoryType;

import java.util.Arrays;

public final class InventoryTypeService {
    public InventoryType getByArray(int[][] array){
        int columns = array.length;
        int rows = array[0].length;

        return Arrays.stream(SupportedInventoryTypes.values())
                .filter(inventoryType-> inventoryType.columns == columns && inventoryType.rows == rows)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Inventory type not found"))
                .getBukkitInventoryType();
    }

    public int getRowBySlotAndInventoryType(int slot, InventoryType inventoryType){
        int rows = SupportedInventoryTypes.valueOf(inventoryType.toString()).getRows();

        return slot / rows;
    }

    public int getColumnBySlotAndInventoryType(int slot, InventoryType inventoryType){
        int cols = SupportedInventoryTypes.valueOf(inventoryType.toString()).getColumns();
        int rows = SupportedInventoryTypes.valueOf(inventoryType.toString()).getRows();

        return cols - (slot / rows);
    }

    private enum SupportedInventoryTypes {
        HOPPER(InventoryType.HOPPER, 1 ,9);

        @Getter private final InventoryType bukkitInventoryType;
        @Getter private final int rows;
        @Getter private final int columns;

        SupportedInventoryTypes(InventoryType type, int rows, int columns) {
            this.bukkitInventoryType = type;
            this.rows = rows;
            this.columns = columns;
        }
    }
}
