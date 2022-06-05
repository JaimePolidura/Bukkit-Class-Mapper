package es.jaimetruman.menus;

import es.jaimetruman.ItemUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@ToString
public final class Page {
    @Getter private final Inventory inventory;
    @Getter private final int[][] itemsNums;
    @Getter private final Map<Integer, Integer> lastItemsAddedSlots;

    public void addItem(ItemStack item, int itemNum, int slot){
        InventoryType inventoryType = SupportedInventoryType.getByArray(this.getItemsNums())
                .getBukkitInventoryType();
        int row = SupportedInventoryType.getRowBySlot(slot, inventoryType);
        int column = SupportedInventoryType.getColumnBySlot(slot, inventoryType);

        this.inventory.setItem(slot, item);
        this.itemsNums[row][column] = itemNum;
        this.lastItemsAddedSlots.put(itemNum, slot);
    }

    public void setItem(int slot, ItemStack newItem){
        this.inventory.setItem(slot, newItem);
    }

    public void setItemLore(int slot, List<String> newLore){
        ItemStack itemToEdit = inventory.getItem(slot);
        ItemMeta itemToEditMeta = itemToEdit.getItemMeta();
        itemToEditMeta.setLore(newLore);
        itemToEdit.setItemMeta(itemToEditMeta);
        inventory.setItem(slot, itemToEdit);
    }

    public void setItemLore(int slot, int index, String newLore){
        ItemStack itemToEdit = inventory.getItem(slot);
        ItemStack itemEdited = ItemUtils.setLore(itemToEdit, index, newLore);
        inventory.setItem(slot, itemEdited);
    }

    public List<ItemStack> getItemsByItemNum(int itemNum){
        List<ItemStack> toReturn = new ArrayList<>();
        int maxRows = this.itemsNums.length;
        int maxCols = this.itemsNums[0].length;

        for (int i = 0; i < this.itemsNums.length; i++) {
            for (int j = 0; j < this.itemsNums[i].length; j++) {
                if(itemsNums[i][j] == itemNum)
                    toReturn.add(inventory.getItem(
                            i * maxRows + j
                    ));
            }
        }

        return toReturn;
    }
}
