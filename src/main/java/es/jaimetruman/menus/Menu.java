package es.jaimetruman.menus;

import es.jaimetruman.menus.configuration.MenuConfiguration;
import lombok.Getter;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public abstract class Menu {
    private int actualPage;
    @Getter private final int[][] itemsNums;
    @Getter private List<Page> pages;

    public Menu() {
        this.itemsNums = this.items();
        this.actualPage = 0;
        this.pages = new ArrayList<>();
    }

    public abstract int[][] items();
    public abstract MenuConfiguration configuration();

    public void addAll(List<Page> pages){
        this.pages.addAll(pages);
    }

    public Inventory getActualPageInventory(){
        return this.pages.get(this.actualPage).getInventory();
    }

    public Page forward(){
        if(this.actualPage >= this.pages.size()) return this.pages.get(this.pages.size() - 1);

        this.actualPage++;
        return this.pages.get(this.actualPage);
    }

    public Page backward(int pageNumber){
        if(this.actualPage == 0) return this.pages.get(0);

        this.actualPage--;
        return this.pages.get(this.actualPage);
    }
}
