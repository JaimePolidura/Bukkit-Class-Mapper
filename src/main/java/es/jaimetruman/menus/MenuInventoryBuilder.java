package es.jaimetruman.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MenuInventoryBuilder {
    private final InventoryTypeService inventoryTypeService;

    public MenuInventoryBuilder() {
        this.inventoryTypeService = InstanceProvider.INVENTORY_TYPE_SERVICE;
    }

    public Inventory build(MenuConfiguration configuration, int[][] items){
        InventoryType inventoryType = this.inventoryTypeService.getByArray(items);
        Inventory inventory = Bukkit.createInventory(null, inventoryType, configuration.getTitle());

        for(int i = 0; i < items.length; i++){
            for(int j = 0; j < items[i].length; j++){
                int itemNum = items[i][j];

                if(itemNum == configuration.getItemAdder().getItemNum()){
                    for (ItemStack item : configuration.getItemAdder().getItems()) {
                        inventory.addItem(item);
                        j++;

                        if(j >= items[i].length){
                            i++;
                            j = 0;
                        }
                    }
                }else{
                    ItemStack itemToAdd = configuration.getItems().get(itemNum);
                    inventory.addItem(itemToAdd == null ? new ItemStack(Material.AIR) : itemToAdd);
                }

            }
        }

        return inventory;
    }
}
