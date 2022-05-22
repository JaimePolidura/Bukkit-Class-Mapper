package es.jaimetruman.menus.refreshcable;

import es.jaimetruman._shared.utils.ClassMapperInstanceProvider;
import es.jaimetruman.menus.Menu;
import es.jaimetruman.menus.Page;
import es.jaimetruman.menus.SupportedInventoryType;
import es.jaimetruman.menus.repository.OpenMenuRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public final class RefreshcableMenuService {
    private final OpenMenuRepository openMenuRepository;

    public RefreshcableMenuService() {
        this.openMenuRepository = ClassMapperInstanceProvider.OPEN_MENUS_REPOSITORY;
    }

    public synchronized void add(Class<? extends Menu> menuType, ItemStack item){
    }

    public synchronized void delete(Menu menu, int slotItemToDelete){
        int pageNumber = menu.getActualPageNumber();

        for (Menu menuOfOtherPlayer : openMenuRepository.findByMenuType(menu.getClass())) {
            int rowOfItemToDelete = SupportedInventoryType.getRowBySlot(slotItemToDelete, menuOfOtherPlayer.getInventory().getType());
            int columOfItemToDelete = SupportedInventoryType.getColumnBySlot(slotItemToDelete, menuOfOtherPlayer.getInventory().getType());
            Page pageOfToDelete = menuOfOtherPlayer.getPages().get(pageNumber);
            Inventory inventoryOfPage = pageOfToDelete.getInventory();

            if(!menu.getMenuId().equals(menuOfOtherPlayer.getMenuId())){
                inventoryOfPage.clear(slotItemToDelete);
            }


            LastItemInMenuSearchResult lastItemSearchResult = findLastItemInMenu(menuOfOtherPlayer, slotItemToDelete);

            if(lastItemSearchResult == null)  return;

            Page lastPage = lastItemSearchResult.getLastPage();
            Inventory inventoryLastPage = lastPage.getInventory();
            int rowOfLastItem = SupportedInventoryType.getRowBySlot(lastItemSearchResult.getSlot(), menuOfOtherPlayer.getInventory().getType());
            int columOfLastItem = SupportedInventoryType.getColumnBySlot(lastItemSearchResult.getSlot(), menuOfOtherPlayer.getInventory().getType());
            int itemNumOfLastItem = lastItemSearchResult.getLastPage().getItemsNums()[rowOfLastItem][columOfLastItem];

            inventoryOfPage.setItem(slotItemToDelete, lastItemSearchResult.getItemStack());
            inventoryLastPage.clear(lastItemSearchResult.getSlot());
            pageOfToDelete.getItemsNums()[rowOfItemToDelete][columOfItemToDelete] = itemNumOfLastItem;
            lastPage.getItemsNums()[rowOfLastItem][columOfLastItem] = 0;
        }
    }

    private LastItemInMenuSearchResult findLastItemInMenu(Menu menu, int slotItemRemoved) {
        Page lastPage = menu.getPages().get(menu.getPages().size() - 1);
        Inventory lastPageInventory = lastPage.getInventory();
        int[][] lastPageItemNums = lastPage.getItemsNums();
        ItemStack[] itemsInLastPage = lastPage.getInventory().getContents();

        for (int i = 0; i < itemsInLastPage.length; i++) {
            if(i == slotItemRemoved) continue;

            boolean nullItem = itemsInLastPage[i] == null;
            boolean airItem = itemsInLastPage[i] != null && itemsInLastPage[i].getType() == Material.AIR;
            boolean lastPositionInArray = i + 1 >= itemsInLastPage.length;

            if(nullItem || airItem || lastPositionInArray){
                return i - 1 >= 0 ?
                        new LastItemInMenuSearchResult(i - 1, itemsInLastPage[i - 1], lastPage)
                        : null;
            }
        }

        return null;
    }

    @AllArgsConstructor
    @ToString
    private static class LastItemInMenuSearchResult{
        @Getter private final int slot;
        @Getter private final ItemStack itemStack;
        @Getter private final Page lastPage;
    }

}
