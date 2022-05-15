package es.jaimetruman.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MenuInventoryBuilder {
    public Inventory build(Menu menu){
        SupportedInventoryType supportedInventoryType = SupportedInventoryType.getByArray(menu.getItems());
        Inventory inventory = this.createBaseInventory(menu.configuration(), supportedInventoryType);
        List<Integer> itemssList = bidimensionalArrayToLinearArray(menu.getItems());
        List<ItemStack> itemsOverflow = new LinkedList<>();
        int totalColumns = menu.getItems()[0].length;
        int totalRows = menu.getItems().length;

        for (int i = 0; i < itemssList.size(); i++) {
            int actualItemNum = itemssList.get(i);
            int row = i / totalColumns;
            int column = i - (totalColumns * row);
            List<ItemStack> itemsToAdd = menu.configuration().getItems().get(actualItemNum);

            if(itemsToAdd != null){
                for (int j = 0; j < itemsToAdd.size(); j++) {
                    if(itemsToAdd.size() > 1 && j > 0) i++;

                    ItemStack itemToAdd = itemsToAdd.get(j);

                    inventory.setItem(i, itemToAdd);

                    if(i >= itemssList.size()){
                        itemsOverflow.add(itemToAdd);
                    }else {
                        menu.getItems()[row][column] = actualItemNum;
                    }
                }
            }else{
                inventory.addItem(new ItemStack(Material.AIR));
                menu.getItems()[row][column] = actualItemNum;
            }
        }

        return inventory;
    }

    private Inventory createBaseInventory(MenuConfiguration configuration, SupportedInventoryType supportedInventoryType) {
        return supportedInventoryType.getSize() % 9 == 0 ?
                Bukkit.createInventory(null, supportedInventoryType.getSize(), configuration.getTitle()) :
                Bukkit.createInventory(null, supportedInventoryType.getBukkitInventoryType(), configuration.getTitle());
    }

    private List<Integer> bidimensionalArrayToLinearArray(int[][] array){
        List<Integer> toReturn = new ArrayList<>(array.length * array[0].length);

        for (int i = 0; i < array.length; i++)
            for (int j = 0; j < array[i].length; j++)
                toReturn.add(array[i][j]);

        return toReturn;
    }
}
