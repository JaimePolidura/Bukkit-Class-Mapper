package es.jaimetruman.menus.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

@AllArgsConstructor
public class MenuConfiguration {
    @Getter private final Map<Integer, List<ItemStack>> items;
    @Getter private final Map<Integer, Consumer<InventoryClickEvent>> onClickEventListeners;
    @Getter private final Consumer<InventoryCloseEvent> onCloseEventListener;
    @Getter private final String title;
    @Getter private final boolean fixedItems;
    @Getter private final int breakpointItemNum;
    @Getter private final PaginationConfiguration menuPaginationConfiguration;

    public static MenuConfigurationBuilder builder(){
        return new MenuConfigurationBuilder();
    }

    public boolean isPaginated(){
        return this.menuPaginationConfiguration == null;
    }

    public static class MenuConfigurationBuilder{
        private Map<Integer, List<ItemStack>> items;
        private Map<Integer, Consumer<InventoryClickEvent>> onClickEventListeners;
        private Consumer<InventoryCloseEvent> onCloseEventListener;
        private PaginationConfiguration menuPaginationConfiguration;
        private String title;
        private boolean fixedItems;
        private int breakpointItemNum;

        public MenuConfigurationBuilder(){
            this.items = new HashMap<>();
            this.onClickEventListeners = new HashMap<>();
            this.breakpointItemNum = -1;
        }

        public MenuConfiguration build(){
            return new MenuConfiguration(items, onClickEventListeners, onCloseEventListener,
                    title, fixedItems, breakpointItemNum, menuPaginationConfiguration);
        }

        public MenuConfigurationBuilder fixedItems(){
            this.fixedItems = true;
            return this;
        }

        public MenuConfigurationBuilder paginated(PaginationConfiguration paginationConfiguration){
            this.menuPaginationConfiguration = paginationConfiguration;
            this.items.put(paginationConfiguration.getBackward().getItemNum(), Collections.singletonList(paginationConfiguration.getBackward().getItemStack()));
            this.items.put(paginationConfiguration.getForward().getItemNum(), Collections.singletonList(paginationConfiguration.getForward().getItemStack()));
            return this;
        }

        public MenuConfigurationBuilder itemsMap(Map<Integer, ItemStack> items){
            for (Map.Entry<Integer, ItemStack> itemsEntry : items.entrySet()){
                this.items.put(itemsEntry.getKey(), Collections.singletonList(itemsEntry.getValue()));
            }

            return this;
        }

        public MenuConfigurationBuilder item(int itemNum, ItemStack item){
            this.items.put(itemNum, Collections.singletonList(item));
            return this;
        }

        public MenuConfigurationBuilder item(int itemNum, ItemStack item, Consumer<InventoryClickEvent> onClick){
            this.items.put(itemNum, Collections.singletonList(item));
            this.onClickEventListeners.put(itemNum, onClick);
            return this;
        }

        public MenuConfigurationBuilder basicItemsMap(Map<Integer, Material> items){
            items.forEach((itemNum, itemMaterial) -> {
                this.items.put(itemNum, Collections.singletonList(new ItemStack(itemMaterial)));
            });

            return this;
        }

        public MenuConfigurationBuilder item(int itemNum, Material itemMaterial){
            this.items.put(itemNum, Collections.singletonList(new ItemStack(itemMaterial)));
            return this;
        }

        public MenuConfigurationBuilder item(int itemNum, Material itemMaterial, Consumer<InventoryClickEvent> onClick){
            this.items.put(itemNum, Collections.singletonList(new ItemStack(itemMaterial)));
            this.onClickEventListeners.put(itemNum, onClick);
            return this;
        }

        public MenuConfigurationBuilder onClick(int itemNum, Consumer<InventoryClickEvent> listener){
            this.onClickEventListeners.put(itemNum, listener);
            return this;
        }

        public MenuConfigurationBuilder items(int itemNum, List<ItemStack> items){
            this.items.put(itemNum, items);
            return this;
        }

        public MenuConfigurationBuilder items(int itemNum, List<ItemStack> items, Consumer<InventoryClickEvent> onClick){
            this.items.put(itemNum, items);
            this.onClickEventListeners.put(itemNum, onClick);
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

        public MenuConfigurationBuilder breakpoint(int itemNum){
            this.breakpointItemNum = itemNum;
            return this;
        }

        public MenuConfigurationBuilder breakpoint(int itemNum, Consumer<InventoryClickEvent> clickEvent){
            this.breakpointItemNum = itemNum;
            this.onClickEventListeners.put(itemNum, clickEvent);
            return this;
        }

        public MenuConfigurationBuilder breakpoint(int itemNum, Material material){
            this.breakpointItemNum = itemNum;
            this.items.put(itemNum, Collections.singletonList(new ItemStack(material)));
            return this;
        }

        public MenuConfigurationBuilder breakpoint(int itemNum, Material material, Consumer<InventoryClickEvent> clickEvent){
            this.breakpointItemNum = itemNum;
            this.items.put(itemNum, Collections.singletonList(new ItemStack(material)));
            this.onClickEventListeners.put(itemNum, clickEvent);
            return this;
        }

        public MenuConfigurationBuilder breakpoint(int itemNum, ItemStack item){
            this.breakpointItemNum = itemNum;
            this.items.put(itemNum, Collections.singletonList(item));
            return this;
        }

        public MenuConfigurationBuilder breakpoint(int itemNum, ItemStack item, Consumer<InventoryClickEvent> clickEvent){
            this.breakpointItemNum = itemNum;
            this.items.put(itemNum, Collections.singletonList(item));
            this.onClickEventListeners.put(itemNum, clickEvent);
            return this;
        }
    }
}
