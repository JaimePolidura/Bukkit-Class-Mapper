package es.jaimetruman.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MenuInventoryBuilder {
    public Inventory build(MenuConfiguration configuration, int[][] items){
        SupportedInventoryType supportedInventoryType = SupportedInventoryType.getByArray(items);
        Inventory inventory = this.createBaseInventory(configuration, supportedInventoryType);
        boolean hasItemAdder = configuration.getItemAdder() != null;
        int totalRows = items.length;
        int totalColumns = items[0].length;

        for(int row = 0; row < totalRows; row++){
            for(int column = 0; column < totalColumns; column++){
                int itemNum = items[row][column];
                int actualSlot = row * totalColumns + column;

                if(hasItemAdder && itemNum == configuration.getItemAdder().getItemNum()){
                    for (ItemStack item : configuration.getItemAdder().getItems()) {
                        inventory.addItem(item);
                        column++;

                        if(column >= items[row].length){
                            row++;
                            column = 0;
                        }
                    }
                }else{
                    ItemStack itemToAdd = configuration.getItems().get(itemNum);
                    inventory.setItem(actualSlot, itemToAdd == null ? new ItemStack(Material.AIR) : itemToAdd);
                }

            }
        }

        return inventory;
    }

    private Inventory createBaseInventory(MenuConfiguration configuration, SupportedInventoryType supportedInventoryType) {
        return supportedInventoryType.getSize() % 9 == 0 ?
                Bukkit.createInventory(null, supportedInventoryType.getSize(), configuration.getTitle()) :
                Bukkit.createInventory(null, supportedInventoryType.getBukkitInventoryType(), configuration.getTitle());
    }
}
