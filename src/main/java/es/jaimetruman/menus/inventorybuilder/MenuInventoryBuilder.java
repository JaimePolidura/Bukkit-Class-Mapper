package es.jaimetruman.menus.inventorybuilder;

import es.jaimetruman.menus.Menu;
import es.jaimetruman.menus.SupportedInventoryType;
import es.jaimetruman.menus.configuration.MenuConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MenuInventoryBuilder {
    public MenuInventory build(Menu menu){
        SupportedInventoryType supportedInventoryType = SupportedInventoryType.getByArray(menu.getItems());
        Inventory inventory = this.createBaseInventory(menu.configuration(), supportedInventoryType);
        List<Integer> itemssList = bidimensionalArrayToLinearArray(menu.getItems());
        List<ItemStack> itemsOverflow = new LinkedList<>();
        int totalColumns = menu.getItems()[0].length;
        int totalRows = menu.getItems().length;
        int breakpointItemNum = menu.configuration().getBreakpointItemNum();

        for (int i = 0; i < itemssList.size(); i++) {
            int actualItemNum = itemssList.get(i);
            List<ItemStack> itemsToAdd = menu.configuration().getItems().get(actualItemNum);

            if(itemsToAdd == null || itemsToAdd.size() == 0) continue;

            for (int j = 0; j < itemsToAdd.size(); j++) {
                if(itemsToAdd.size() > 1 && j > 0) i++;

                int row = SupportedInventoryType.getRowBySlotAndInventoryType(i, inventory.getType());
                int column = SupportedInventoryType.getColumnBySlotAndInventoryType(i, inventory.getType());
                boolean isInBreakpoint = menu.getItems()[row][column] == breakpointItemNum;

                if(isInBreakpoint){
                    inventory.setItem(i, menu.configuration().getItems().get(breakpointItemNum) == null ?
                                    new ItemStack(Material.AIR) :
                                    menu.configuration().getItems().get(breakpointItemNum).get(0)
                    );

                    itemsOverflow.addAll(itemsToAdd.subList(j, itemsToAdd.size()));

                    break;
                }

                ItemStack itemToAdd = itemsToAdd.get(j);
                boolean itemOverflow = i >= itemssList.size();

                if(itemOverflow){
                    itemsOverflow.add(itemToAdd);
                }else{
                    menu.getItems()[row][column] = actualItemNum;
                    inventory.setItem(i, itemToAdd);
                }
            }
        }

        return new MenuInventory(inventory, itemsOverflow);
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
