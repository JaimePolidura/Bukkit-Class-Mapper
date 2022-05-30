package es.jaimetruman.menus;

import es.jaimetruman.ItemUtils;
import es.jaimetruman.menus.configuration.MenuConfiguration;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public abstract class Menu {
    @Getter private final Map<String, Object> properties;
    @Getter private final UUID menuId;
    @Getter private final int[][] baseItemNums;
    @Getter private int actualPageNumber;
    @Getter private List<Page> pages;

    public Menu() {
        this.baseItemNums = this.items();
        this.actualPageNumber = 0;
        this.pages = new ArrayList<>();
        this.menuId = UUID.randomUUID();
        this.properties = new HashMap<>();
        this.properties.putAll(this.configuration().getProperties());
    }

    public abstract int[][] items();
    public abstract MenuConfiguration configuration();

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

    public final void setItem(int slot, ItemStack newItemStack){
        this.getActualPage().getInventory().setItem(slot, newItemStack);
    }

    public final void setItemLore(int slot, List<String> newLore){
        ItemStack itemToEdit = this.getActualPage().getInventory().getItem(slot);
        ItemMeta itemToEditMeta = itemToEdit.getItemMeta();
        itemToEditMeta.setLore(newLore);
        itemToEdit.setItemMeta(itemToEditMeta);
        this.getActualPage().getInventory().setItem(slot, itemToEdit);
    }

    public final void setItemLore(int slot, int index, String newLore){
        ItemStack itemToEdit = this.getActualPage().getInventory().getItem(slot);
        ItemStack itemEdited = ItemUtils.setLore(itemToEdit, index, newLore);
        this.getActualPage().getInventory().setItem(slot, itemEdited);
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
}
