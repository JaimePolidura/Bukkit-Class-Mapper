package es.jaimetruman.menus.eventlisteners;

import es.jaimetruman.menus.Menu;
import es.jaimetruman.menus.OnMenuClicked;
import es.jaimetruman.menus.modules.confirmation.OnConfirmationMenuClicked;
import es.jaimetruman.menus.modules.numberselector.OnNumberSelectorMenuClick;
import es.jaimetruman.menus.modules.pagination.OnPaginationMenuClicked;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public final class OnMenuClickedListeners {
    private static final Set<OnMenuClicked> listeners;

    static {
        listeners = new HashSet<>();

        OnMenuClickedListeners.listen(new OnConfirmationMenuClicked());
        OnMenuClickedListeners.listen(new OnNumberSelectorMenuClick());
        OnMenuClickedListeners.listen(new OnPaginationMenuClicked());
    }


    public static void listen(OnMenuClicked onMenuClicked){
        listeners.add(onMenuClicked);
    }

    public static void notify(Player player, Menu menu, int itemNumClicked){
        listeners.forEach(listener -> {
            listener.on(player, menu, itemNumClicked);
        });
    }
}
