package es.jaimetruman.menus.modules.numberselector;

import es.jaimetruman.menus.Menu;
import es.jaimetruman.menus.OnMenuClicked;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public final class OnNumberSelectorMenuClick implements OnMenuClicked {
    @Override
    public void on(Player player, Menu menu, int itemNumClicked) {
        if(hasClickedNumberSelectorItem(menu, itemNumClicked)){
            this.performNumberSelectorClicked(menu, itemNumClicked);
        }
    }

    private boolean hasClickedNumberSelectorItem(Menu menu, int itemNumClicekd){
        return menu.configuration().isNumberSelector() && menu.configuration().getNumberSelectorMenuConfiguration()
                .getItems().get(itemNumClicekd) != null;
    }

    private void performNumberSelectorClicked(Menu menu, int itemNum){
        NumberSelectorMenuConfiguration configuration = menu.configuration().getNumberSelectorMenuConfiguration();
        NumberSelectorControllItem controllItem = configuration.getItems().get(itemNum);
        String valuePropertyName = configuration.getValuePropertyName();
        double actualValue = menu.getPropertyDouble(valuePropertyName);
        boolean isIncrease = controllItem.getActionType() == NumberSelectActionType.INCREASE;

        double newValue = isIncrease ? actualValue + controllItem.getValueToChange() : actualValue - controllItem.getValueToChange();
        boolean newValueInsideBounds = newValue >= configuration.getMinValue() && newValue <= configuration.getMaxValue();

        if(newValueInsideBounds){
            applyNewValueProperty(menu, valuePropertyName, newValue);
            callOnValueChanged(configuration, newValue);
        }
    }

    private void applyNewValueProperty(Menu menu, String valuePropertyName, double newValue) {
        menu.setProperty(valuePropertyName, newValue);
    }

    private void callOnValueChanged(NumberSelectorMenuConfiguration configuration, double newValue) {
        Consumer<Double> onValueChangedConsumer = configuration.getOnValueChanged();

        if(onValueChangedConsumer != null)
            onValueChangedConsumer.accept(newValue);
    }

}
