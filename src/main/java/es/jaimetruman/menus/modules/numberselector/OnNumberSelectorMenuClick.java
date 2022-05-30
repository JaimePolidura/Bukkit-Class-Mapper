package es.jaimetruman.menus.modules.numberselector;

import es.jaimetruman._shared.utils.ClassMapperInstanceProvider;
import es.jaimetruman.menus.Menu;
import es.jaimetruman.menus.OnMenuClicked;
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
        return menu.configuration().isNumberSelector() && menu.configuration().getNumberSelectorMenuConfiguration()
                .getItems().get(itemNumClicekd) != null;
    }
}
