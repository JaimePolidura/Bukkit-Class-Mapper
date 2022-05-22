package es.jaimetruman.menus.repository;

import es.jaimetruman.menus.Menu;
import es.jaimetruman.menus.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class StaticMenuRepository {
    private final Map<Class<? extends Menu>, List<Page>> menus;

    public StaticMenuRepository() {
        this.menus = new HashMap<>();
    }

    public void save(Menu menu){
        this.menus.put(menu.getClass(), menu.getPages());
    }

    public Optional<List<Page>> findByMenuClass(Class<? extends Menu> menuClass){
        return Optional.ofNullable(this.menus.get(menuClass));
    }
}
