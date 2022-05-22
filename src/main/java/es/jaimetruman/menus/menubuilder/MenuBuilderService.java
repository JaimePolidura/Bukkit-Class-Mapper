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

                int row = SupportedInventoryType.getRowBySlot(i, inventoryOfPageZero.getType());
                int column = SupportedInventoryType.getColumnBySlot(i, inventoryOfPageZero.getType());

                if(isBreakpoint(menu, row, column)){
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

        if(!itemsOverflow.isEmpty())
            pages.addAll(createMorePages(itemsOverflow, menu, supportedInventoryType, itemNumOverflow));

        return new MenuBuildResult(pages);
    }

    private List<Page> createMorePages(List<ItemStack> itemsOverflow, Menu menu, SupportedInventoryType inventoryType, int itemNum) {
        List<Page> pages = new ArrayList<>();
        int maxItemsPerPage = menu.items().length * menu.items()[0].length;
        int breakpointItemNum = menu.configuration().getBreakpointItemNum();
        int pagesToAdd = itemsOverflow.size() % maxItemsPerPage == 0 ?
                itemsOverflow.size() / maxItemsPerPage :
                (itemsOverflow.size() / maxItemsPerPage) + 1;

        for (int i = 0; i < pagesToAdd; i++) {
            Inventory inventory = this.createBaseInventory(menu.configuration(), inventoryType);
            List<ItemStack> itemsToAdd = sublist(itemsOverflow, i * maxItemsPerPage, i * maxItemsPerPage + maxItemsPerPage);
            int[][] itemsNums = new int[menu.items().length][menu.items()[0].length];
            boolean hasPassedBreakpoint = false;

            for (int j = 0; j < maxItemsPerPage; j++) {
                ItemStack itemToAdd = j >= itemsToAdd.size() ? null : itemsToAdd.get(j);
                int row = SupportedInventoryType.getRowBySlot(j, inventoryType.getBukkitInventoryType());
                int column = SupportedInventoryType.getColumnBySlot(j, inventoryType.getBukkitInventoryType());

                if(this.isBreakpoint(menu, row, column)) {
                    inventory.addItem(menu.configuration().getItems().get(breakpointItemNum) == null ?
                            new ItemStack(Material.AIR) :
                            menu.configuration().getItems().get(breakpointItemNum).get(0)
                    );
                    itemsNums[row][column] = breakpointItemNum;
                    hasPassedBreakpoint = true;

                }else if(isGoBackward(menu, row, column)){
                    int itemNumOriginal = menu.items()[row][column];
                    inventory.setItem(j, menu.configuration().getPaginationConfiguration().getBackward().getItemStack());
                    itemsNums[row][column] = itemNumOriginal;

                }else if(isGoForward(menu, row, column)) {
                    int itemNumOriginal = menu.items()[row][column];
                    inventory.setItem(j, menu.configuration().getPaginationConfiguration().getForward().getItemStack());
                    itemsNums[row][column] = itemNumOriginal;

                }else if(hasPassedBreakpoint || itemToAdd == null){
                    itemsNums[row][column] = 0;

                }else{
                    inventory.addItem(itemToAdd);
                    itemsNums[row][column] = itemNum;
                }
            }

            pages.add(new Page(inventory, itemsNums));
        }

        return pages;
    }

    private boolean isGoForward(Menu menu, int row, int column){
        int itemNum = menu.items()[row][column];

        return menu.configuration().isPaginated() && menu.configuration().getPaginationConfiguration().getForward().getItemNum() == itemNum;
    }

    private boolean isGoBackward(Menu menu, int row, int column){
        int itemNum = menu.items()[row][column];

        return menu.configuration().isPaginated() && menu.configuration().getPaginationConfiguration().getBackward().getItemNum() == itemNum;
    }

    private boolean isBreakpoint(Menu menu, int row, int column) {
        return menu.getItemsNums()[row][column] == menu.configuration().getBreakpointItemNum();
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
