package es.jaimetruman.menus.menubuilder;

import es.jaimetruman.menus.Menu;
import es.jaimetruman.menus.Page;
import es.jaimetruman.menus.SupportedInventoryType;
import es.jaimetruman.menus.configuration.MenuConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static es.jaimetruman._shared.utils.CollectionUtils.*;

public class MenuBuilderService {
    public MenuBuildResult build(Menu menu){
        SupportedInventoryType supportedInventoryType = SupportedInventoryType.getByArray(menu.getItemsNums());
        Inventory inventoryOfPageZero = this.createBaseInventory(menu.configuration(), supportedInventoryType);
        List<Integer> itemssList = bidimensionalArrayToLinearArray(menu.getItemsNums());
        List<ItemStack> itemsOverflow = new LinkedList<>();
        List<Page> pages = new ArrayList<>();
        int totalColumns = menu.getItemsNums()[0].length;
        int totalRows = menu.getItemsNums().length;
        int breakpointItemNum = menu.configuration().getBreakpointItemNum();
        int itemNumOverflow = 0;

        for (int i = 0; i < itemssList.size(); i++) {
            int actualItemNum = itemssList.get(i);
            List<ItemStack> itemsToAdd = menu.configuration().getItems().get(actualItemNum);

            if(itemsToAdd == null || itemsToAdd.size() == 0) continue;

            for (int j = 0; j < itemsToAdd.size(); j++) {
                if(itemsToAdd.size() > 1 && j > 0) i++;

                int row = SupportedInventoryType.getRowBySlotAndInventoryType(i, inventoryOfPageZero.getType());
                int column = SupportedInventoryType.getColumnBySlotAndInventoryType(i, inventoryOfPageZero.getType());
                boolean isInBreakpoint = menu.getItemsNums()[row][column] == breakpointItemNum;

                if(isInBreakpoint){
                    inventoryOfPageZero.setItem(i, menu.configuration().getItems().get(breakpointItemNum) == null ?
                                    new ItemStack(Material.AIR) :
                                    menu.configuration().getItems().get(breakpointItemNum).get(0)
                    );

                    if(j > 0)
                        itemsOverflow.addAll(itemsToAdd.subList(j, itemsToAdd.size()));

                    break;
                }

                ItemStack itemToAdd = itemsToAdd.get(j);
                boolean itemOverflow = i >= itemssList.size();

                if(itemOverflow){
                    itemNumOverflow = actualItemNum;
                    itemsOverflow.add(itemToAdd);
                }else{
                    menu.getItemsNums()[row][column] = actualItemNum;
                    inventoryOfPageZero.setItem(i, itemToAdd);
                }
            }
        }

        pages.add(new Page(inventoryOfPageZero, menu.getItemsNums()));

        System.out.println(new Page(inventoryOfPageZero, menu.getItemsNums()));

        if(!itemsOverflow.isEmpty())
            pages.addAll(createMorePages(itemsOverflow, menu, supportedInventoryType, itemNumOverflow));

        return new MenuBuildResult(pages);
    }

    private List<Page> createMorePages(List<ItemStack> itemsOverflow, Menu menu, SupportedInventoryType inventoryType, int itemNum) {
        List<Page> pages = new ArrayList<>();
        int maxItemsPerPage = menu.items().length * menu.items()[0].length;
        int pagesToAdd = pages.size() % maxItemsPerPage == 0 ?
                pages.size() / maxItemsPerPage :
                (pages.size() / maxItemsPerPage) + 1;

        for (int i = 0; i < pagesToAdd; i++) {
            Inventory inventory = this.createBaseInventory(menu.configuration(), inventoryType);
            List<ItemStack> itemsToAdd = sublist(itemsOverflow, i * maxItemsPerPage, i * maxItemsPerPage + maxItemsPerPage);
            int[][] itemsNums = new int[menu.items().length][menu.items()[0].length];

            for (int j = 0; j < itemsToAdd.size(); j++) {
                int row = SupportedInventoryType.getRowBySlotAndInventoryType(j, inventoryType.getBukkitInventoryType());
                int column = SupportedInventoryType.getColumnBySlotAndInventoryType(j, inventoryType.getBukkitInventoryType());

                inventory.addItem(itemsToAdd.get(j));
                itemsNums[row][column] = itemNum;
            }

            pages.add(new Page(inventory, itemsNums));
        }

        return pages;
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
