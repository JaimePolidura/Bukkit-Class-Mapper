package es.jaimetruman.menus;

import es.jaimetruman.menus.configuration.MenuConfiguration;
import lombok.Getter;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public abstract class Menu {
    private int actualPage;
    private List<Page> pages;
    @Getter private final int[][] itemsNums;

    public Menu() {
        this.itemsNums = this.items();
        this.actualPage = 0;
        this.pages = new ArrayList<>();
    }

    public abstract int[][] items();
    public abstract MenuConfiguration configuration();

    public final void addAll(List<Page> pages){
        this.pages.addAll(pages);
    }

    public final Inventory getActualPageInventory(){
        return this.pages.get(this.actualPage).getInventory();
    }

    public final Page forward(){
        if(this.actualPage + 1 >= this.pages.size()) return this.pages.get(this.pages.size() - 1);

        this.actualPage++;
        return this.pages.get(this.actualPage);
    }

    public final Page backward(){
        if(this.actualPage == 0) return this.pages.get(0);

        this.actualPage--;
        return this.pages.get(this.actualPage);
    }
}
