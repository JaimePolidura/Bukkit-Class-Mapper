package es.bukkitclassmapper.menus.modules.numberselector;

import es.bukkitclassmapper._shared.utils.ClassMapperInstanceProvider;
import es.bukkitclassmapper.menus.Menu;
import es.bukkitclassmapper.menus.OnMenuClicked;
import org.bukkit.entity.Player;

public final class OnNumberSelectorMenuClick implements OnMenuClicked {
    private final NumberSelectorService numberSelectorService;

    public OnNumberSelectorMenuClick() {
        this.numberSelectorService = ClassMapperInstanceProvider.NUMBER_SELECTOR_SERVICE;
    }

    @Override
    public void on(Player player, Menu menu, int itemNumClicked) {
        if(hasClickedNumberSelectorItem(menu, itemNumClicked)){
            this.numberSelectorService.performNumberSelectorClicked(menu, itemNumClicked);
        }
    }

    private boolean hasClickedNumberSelectorItem(Menu menu, int itemNumClicekd){
        return menu.getConfiguration().isNumberSelector() && menu.getConfiguration().getNumberSelectorMenuConfiguration()
                .getItems().get(itemNumClicekd) != null;
    }
}
