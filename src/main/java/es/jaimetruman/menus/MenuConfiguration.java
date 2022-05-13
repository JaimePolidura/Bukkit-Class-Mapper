package es.jaimetruman.menus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@AllArgsConstructor
public class MenuConfiguration {
    @Getter private final Map<Integer, ItemStack> items;
    @Getter private final ItemAdder itemAdder;
    @Getter private final Map<Integer, Consumer<InventoryClickEvent>> onClickEventListeners;
    @Getter private final Consumer<InventoryCloseEvent> onCloseEventListener;
    @Getter private final String title;
    @Getter private final boolean fixedItems;

    public static MenuConfigurationBuilder builder(){
        return new MenuConfigurationBuilder();
    }

    public static class MenuConfigurationBuilder{
        private Map<Integer, ItemStack> items;
        private Map<Integer, Consumer<InventoryClickEvent>> onClickEventListeners;
        private Consumer<InventoryCloseEvent> onCloseEventListener;
        private ItemAdder itemAdder;
        private String title;
        private boolean fixedItems;

        public MenuConfiguration build(){
            return new MenuConfiguration(items, itemAdder, onClickEventListeners,
                    onCloseEventListener, title, fixedItems);
        }

        public MenuConfigurationBuilder fixedItems(){
            this.fixedItems = true;
            return this;
        }

        public MenuConfigurationBuilder items(Map<Integer, ItemStack> items){
            this.items.putAll(items);
            return this;
        }

        public MenuConfigurationBuilder item(int itemNum, ItemStack item){
            this.items.put(itemNum, item);
            return this;
        }

        public MenuConfigurationBuilder basicItems(Map<Integer, Material> items){
            this.items.forEach((itemId, itemMaterial) -> this.items.put(itemId, new ItemStack(itemMaterial)));
            return this;
        }

        public MenuConfigurationBuilder basicItem(int itemNum, Material itemMaterial){
            this.items.put(itemNum, new ItemStack(itemMaterial));
            return this;
        }

        public MenuConfigurationBuilder onClick(int itemNum, Consumer<InventoryClickEvent> listener){
            this.onClickEventListeners.put(itemNum, listener);
            return this;
        }

        public MenuConfigurationBuilder itemAdder(int itemNum, List<ItemStack> itemAdder){
            this.itemAdder = new ItemAdder(itemNum, itemAdder);
            return this;
        }

        public MenuConfigurationBuilder onClose(Consumer<InventoryCloseEvent> onClose){
            this.onCloseEventListener = onClose;
            return this;
        }

        public MenuConfigurationBuilder title(String title){
            this.title = title;
            return this;
        }
    }

    @AllArgsConstructor
    public static class ItemAdder{
        @Getter private final int itemNum;
        @Getter private final List<ItemStack> items;
    }
}
