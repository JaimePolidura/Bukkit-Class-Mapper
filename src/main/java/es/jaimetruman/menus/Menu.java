package es.jaimetruman.menus;

import lombok.Getter;

public abstract class Menu {
    @Getter
    private final int[][] items;

    protected Menu() {
        this.items = this.items();
    }

    public abstract int[][] items();
    public abstract MenuConfiguration configuration();
}
