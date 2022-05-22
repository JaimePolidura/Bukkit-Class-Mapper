package es.jaimetruman.menus;

import es.jaimetruman.menus.configuration.MenuConfiguration;
import lombok.Getter;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Menu {
    @Getter private final UUID menuId;
    @Getter private final int[][] itemsNums;
    @Getter private int actualPageNumber;
    @Getter private List<Page> pages;

    public Menu() {
        this.itemsNums = this.items();
        this.actualPageNumber = 0;
        this.pages = new ArrayList<>();
        this.menuId = UUID.randomUUID();
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
}
