package es.jaimetruman.menus;

import es.jaimetruman.menus.configuration.MenuConfiguration;
import lombok.Getter;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public abstract class Menu {
    private int actualPageIndex;
    @Getter private List<Page> pages;
    @Getter private final int[][] itemsNums;

    public Menu() {
        this.itemsNums = this.items();
        this.actualPageIndex = 0;
        this.pages = new ArrayList<>();
    }

    public abstract int[][] items();
    public abstract MenuConfiguration configuration();

    public final void addPages(List<Page> pages){
        this.pages.addAll(pages);
    }

    public final Inventory getInventory(){
        return this.pages.get(this.actualPageIndex).getInventory();
    }

    public final Page forward(){
        if(this.actualPageIndex + 1 >= this.pages.size()) return this.pages.get(this.pages.size() - 1);

        this.actualPageIndex++;
        return this.pages.get(this.actualPageIndex);
    }

    public final Page backward(){
        if(this.actualPageIndex == 0) return this.pages.get(0);

        this.actualPageIndex--;
        return this.pages.get(this.actualPageIndex);
    }
}
