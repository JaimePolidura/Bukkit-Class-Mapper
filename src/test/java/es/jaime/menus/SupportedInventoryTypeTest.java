package es.jaime.menus;

import es.jaimetruman.menus.SupportedInventoryType;
import org.bukkit.event.inventory.InventoryType;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public final class SupportedInventoryTypeTest {
    @Test
    public void getRowBySlotAndInventoryType(){
        assertEquals(0, SupportedInventoryType.getRowBySlotAndInventoryType(0, InventoryType.CHEST));
        assertEquals(0, SupportedInventoryType.getRowBySlotAndInventoryType(5, InventoryType.CHEST));
        assertEquals(1, SupportedInventoryType.getRowBySlotAndInventoryType(9, InventoryType.CHEST));
        assertEquals(1, SupportedInventoryType.getRowBySlotAndInventoryType(12, InventoryType.CHEST));
        assertEquals(2, SupportedInventoryType.getRowBySlotAndInventoryType(26, InventoryType.CHEST));

        assertEquals(0, SupportedInventoryType.getRowBySlotAndInventoryType(0, InventoryType.HOPPER));
        assertEquals(0, SupportedInventoryType.getRowBySlotAndInventoryType(2, InventoryType.HOPPER));
        assertEquals(0, SupportedInventoryType.getRowBySlotAndInventoryType(4, InventoryType.HOPPER));
    }

    @Test
    public void getColumnBySlotAndInventoryType(){
        assertEquals(SupportedInventoryType.getColumnBySlotAndInventoryType(0, InventoryType.CHEST),0);
        assertEquals(SupportedInventoryType.getColumnBySlotAndInventoryType(5, InventoryType.CHEST),5);
        assertEquals(SupportedInventoryType.getColumnBySlotAndInventoryType(9, InventoryType.CHEST),0);
        assertEquals(SupportedInventoryType.getColumnBySlotAndInventoryType(12, InventoryType.CHEST),3);
        assertEquals(SupportedInventoryType.getColumnBySlotAndInventoryType(26, InventoryType.CHEST),8);

        assertEquals(SupportedInventoryType.getColumnBySlotAndInventoryType(0, InventoryType.HOPPER),0);
        assertEquals(SupportedInventoryType.getColumnBySlotAndInventoryType(2, InventoryType.HOPPER),2);
        assertEquals(SupportedInventoryType.getColumnBySlotAndInventoryType(4, InventoryType.HOPPER),4);
    }
}
