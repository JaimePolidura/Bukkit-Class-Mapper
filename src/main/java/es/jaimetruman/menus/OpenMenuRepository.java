package es.jaimetruman.menus;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class OpenMenuRepository {
    private final Map<String, Menu> menus;

    public OpenMenuRepository() {
        this.menus = new ConcurrentHashMap<>();
    }

    public void save(String jugador, Menu menu){
        this.menus.put(jugador, menu);
    }

    public Optional<Menu> findByPlayerName(String playerName){
        return Optional.ofNullable(this.menus.get(playerName));
    }

    public void deleteByPlayerName(String playerName){
        this.menus.remove(playerName);
    }
}
