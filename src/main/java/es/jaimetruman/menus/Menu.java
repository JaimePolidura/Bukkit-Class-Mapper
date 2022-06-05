package es.jaimetruman.menus;

import es.jaimetruman.menus.configuration.MenuConfiguration;
import lombok.Getter;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static es.jaimetruman._shared.utils.ClassMapperInstanceProvider.*;
import static es.jaimetruman._shared.utils.CollectionUtils.*;

public abstract class Menu {
    @Getter private final Map<String, Object> properties;
    @Getter private final UUID menuId;
    @Getter private final int[][] baseItemNums;
    @Getter private int actualPageNumber;
    @Getter private final MenuConfiguration configuration;
    private List<Page> pages;

    public Menu() {
        this.baseItemNums = this.items();
        this.actualPageNumber = 0;
        this.pages = new ArrayList<>();
        this.menuId = UUID.randomUUID();
        this.properties = new HashMap<>();
        this.configuration = this.configuration();
        this.properties.putAll(this.configuration.getProperties());
    }

    public abstract int[][] items();
    public abstract MenuConfiguration configuration();

    public final List<Page> allPages(){
        return new ArrayList<>(this.pages);
    }

    public final Page getPage(int pageNumber){
        return this.pages.get(pageNumber);
    }

    public final Page getLastPage(){
        return this.pages.get(this.pages.size() - 1);
    }

    public final Page getActualPage(){
        return this.pages.get(actualPageNumber);
    }

    public final void addPages(List<Page> pages){
        this.pages.addAll(pages);
    }

    public final Inventory getInventory(){
        return this.pages.get(this.actualPageNumber).getInventory();
    }

    public final int[][] getActualItemNums(){
        return this.pages.get(this.actualPageNumber).getItemsNums();
    }

    public void setItem(int pageNumber, int slotItem, ItemStack newItem){
        this.getPage(pageNumber).setItem(slotItem, newItem);
    }

    public final void addItem(int slot, ItemStack newItemStack, int itemNum, int pageNumber){
        this.getPage(pageNumber).addItem(newItemStack, itemNum, slot);
    }

    public final void setItemActualPage(int slot, ItemStack newItemStack, int itemNum){
        this.getActualPage().addItem(newItemStack, itemNum, slot);
    }

    public final void setItemLore(int pageNumber, int itemSlot, List<String> newLore){
        this.getPage(pageNumber).setItemLore(itemSlot, newLore);
    }

    public final List<ItemStack> getItemsByItemNum(int itemNum, int pageNumber){
        return this.getPage(pageNumber).getItemsByItemNum(itemNum);
    }

    public final void setItemLore(int pagenNumer, int itemSlot, int indexItemLore, String newLore){
        this.pages.get(pagenNumer).setItemLore(itemSlot, indexItemLore, newLore);
    }

    public final void deleteItem(int slot){
        this.getActualPage().getInventory().setItem(slot, null);
        InventoryType inventoryType = SupportedInventoryType.getByArray(this.baseItemNums).getBukkitInventoryType();
        this.items()[SupportedInventoryType.getRowBySlot(slot, inventoryType)][SupportedInventoryType.getColumnBySlot(slot, inventoryType)] = 0;
    }

    public final Page forward(){
        if(this.actualPageNumber + 1 >= this.pages.size()) return this.pages.get(this.pages.size() - 1);

        this.actualPageNumber++;
        return this.pages.get(this.actualPageNumber);
    }

    public final Page backward(){
        if(this.actualPageNumber == 0) return this.pages.get(0);

        this.actualPageNumber--;
        return this.pages.get(this.actualPageNumber);
    }

    public final Menu setProperty(String key, Object value){
        this.properties.put(key, value);
        return this;
    }

    public final Object getProperty(String key){
        return this.properties.get(key);
    }

    public final double getPropertyDouble(String key){
        return Double.parseDouble(String.valueOf(this.properties.get(key)));
    }

    public final void addItem(int itemNum, ItemStack item){
        Page lastPage = this.getLastPage();
        int slotOfLastItemAdded = lastPage.getLastItemsAddedSlots().get(itemNum);
        int slotOfNextLastItemAdded = slotOfLastItemAdded + 1;
        int ItemNumOfNextLastItemAdded = bidimensionalArrayToLinearArray(this.baseItemNums)
                .get(slotOfLastItemAdded);
        boolean moreItemsCanBeAdded = ItemNumOfNextLastItemAdded == 0;

        if(moreItemsCanBeAdded){
            lastPage.addItem(item, itemNum, slotOfNextLastItemAdded);
        }else{
            Page newPage = MENU_BUILDER_SERVICE.createPage(this.configuration(), this.baseItemNums,
                    new LinkedList<>(Collections.singletonList(item)), itemNum);

            this.pages.add(newPage);
        }
    }
}
