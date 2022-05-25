package es.jaimetruman.menus.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@AllArgsConstructor
public class MenuConfiguration {
    @Getter private final Map<Integer, List<ItemStack>> items;
    @Getter private final Map<Integer, BiConsumer<Player, InventoryClickEvent>> onClickEventListeners;
    @Getter private final Consumer<InventoryCloseEvent> onCloseEventListener;
    @Getter private final String title;
    @Getter private final boolean fixedItems;
    @Getter private final int breakpointItemNum;
    @Getter private final PaginationConfiguration paginationConfiguration;
    @Getter private final ConfirmationConfiguration confirmationConfiguration;
    @Getter private final boolean staticMenu;
    @Getter private final MessagingConfiguration messagingConfiguration;

    public static MenuConfigurationBuilder builder(){
        return new MenuConfigurationBuilder();
    }

    public <T> Consumer<T> getMessageListener(Class<T> messageType){
        return (Consumer<T>) this.messagingConfiguration.getOnMessageEventListeners().get(messageType);
    }

    public boolean hasMessagingConfiguration(){
        return this.messagingConfiguration != null;
    }

    public boolean isPaginated(){
        return this.paginationConfiguration != null;
    }

    public boolean isConfirmation(){
        return this.confirmationConfiguration != null;
    }

    public static class MenuConfigurationBuilder{
        private Map<Integer, List<ItemStack>> items;
        private Map<Integer, BiConsumer<Player, InventoryClickEvent>> onClickEventListeners;
        private Consumer<InventoryCloseEvent> onCloseEventListener;
        private PaginationConfiguration menuPaginationConfiguration;
        private ConfirmationConfiguration confirmationConfiguration;
        private String title;
        private boolean fixedItems;
        private int breakpointItemNum;
        private boolean staticMenu;
        private MessagingConfiguration messagingConfiguration;

        public MenuConfigurationBuilder(){
            this.items = new HashMap<>();
            this.onClickEventListeners = new HashMap<>();
            this.breakpointItemNum = -1;
        }

        public MenuConfiguration build(){
            return new MenuConfiguration(items, onClickEventListeners, onCloseEventListener,
                    title, fixedItems, breakpointItemNum, menuPaginationConfiguration, confirmationConfiguration,
                    staticMenu, this.messagingConfiguration);
        }

        public MenuConfigurationBuilder fixedItems(){
            this.fixedItems = true;
            return this;
        }

        public MenuConfigurationBuilder messaging(MessagingConfiguration messagingConfiguration){
            this.messagingConfiguration = messagingConfiguration;
            return this;
        }

        public MenuConfigurationBuilder confirmation(ConfirmationConfiguration configuration){
            this.confirmationConfiguration = configuration;
            this.items.put(configuration.getAccept().getItemNum(), Collections.singletonList(configuration.getAccept().getItemStack()));
            this.onClickEventListeners.put(configuration.getAccept().getItemNum(), configuration.getAccept().getOnClick());

            this.items.put(configuration.getCancel().getItemNum(), Collections.singletonList(configuration.getCancel().getItemStack()));
            this.onClickEventListeners.put(configuration.getCancel().getItemNum(), configuration.getCancel().getOnClick());


            return this;
        }

        public MenuConfigurationBuilder paginated(PaginationConfiguration paginationConfiguration){
            this.menuPaginationConfiguration = paginationConfiguration;
            this.items.put(paginationConfiguration.getBackward().getItemNum(), Collections.singletonList(paginationConfiguration.getBackward().getItemStack()));
            this.items.put(paginationConfiguration.getForward().getItemNum(), Collections.singletonList(paginationConfiguration.getForward().getItemStack()));
            if(this.breakpointItemNum == -1)
                this.breakpointItemNum = paginationConfiguration.getBackward().getItemNum();

            return this;
        }

        public MenuConfigurationBuilder staticMenu(){
            this.staticMenu = true;
            return this;
        }

        public MenuConfigurationBuilder itemsMap(Map<Integer, ItemStack> items){
            for (Map.Entry<Integer, ItemStack> itemsEntry : items.entrySet())
                this.items.put(itemsEntry.getKey(), Collections.singletonList(itemsEntry.getValue()));

            return this;
        }

        public MenuConfigurationBuilder item(int itemNum, ItemStack item){
            this.items.put(itemNum, Collections.singletonList(item));
            return this;
        }

        public MenuConfigurationBuilder item(int itemNum, ItemStack item, BiConsumer<Player, InventoryClickEvent> onClick){
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

        public MenuConfigurationBuilder item(int itemNum, Material itemMaterial, BiConsumer<Player, InventoryClickEvent> onClick){
            this.items.put(itemNum, Collections.singletonList(new ItemStack(itemMaterial)));
            this.onClickEventListeners.put(itemNum, onClick);
            return this;
        }

        public MenuConfigurationBuilder onClick(int itemNum, BiConsumer<Player, InventoryClickEvent> listener){
            this.onClickEventListeners.put(itemNum, listener);
            return this;
        }

        public MenuConfigurationBuilder items(int itemNum, List<ItemStack> items){
            this.items.put(itemNum, items);
            return this;
        }

        public MenuConfigurationBuilder items(int itemNum, List<ItemStack> items, BiConsumer<Player, InventoryClickEvent> onClick){
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

        public MenuConfigurationBuilder breakpoint(int itemNum, BiConsumer<Player, InventoryClickEvent> clickEvent){
            this.breakpointItemNum = itemNum;
            this.onClickEventListeners.put(itemNum, clickEvent);
            return this;
        }

        public MenuConfigurationBuilder breakpoint(int itemNum, Material material){
            this.breakpointItemNum = itemNum;
            this.items.put(itemNum, Collections.singletonList(new ItemStack(material)));
            return this;
        }

        public MenuConfigurationBuilder breakpoint(int itemNum, Material material, BiConsumer<Player, InventoryClickEvent> clickEvent){
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

        public MenuConfigurationBuilder breakpoint(int itemNum, ItemStack item, BiConsumer<Player, InventoryClickEvent> clickEvent){
            this.breakpointItemNum = itemNum;
            this.items.put(itemNum, Collections.singletonList(item));
            this.onClickEventListeners.put(itemNum, clickEvent);
            return this;
        }
    }
}
