package es.jaimetruman.menus.modules.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public final class PaginationConfiguration {
    @Getter private final PaginationControllItem forward;
    @Getter private final PaginationControllItem backward;

    public static PaginationConfigurationBuilder builder(){
        return new PaginationConfigurationBuilder();
    }

    public static class PaginationConfigurationBuilder {
        private PaginationControllItem forward;
        private PaginationControllItem backward;

        public PaginationConfiguration build(){
            return new PaginationConfiguration(forward, backward);
        }

        public PaginationConfigurationBuilder forward(int itemNum, Material material){
            this.forward = new PaginationControllItem(itemNum, new ItemStack(material), PaginationControlAction.FORWARD);
            return this;
        }

        public PaginationConfigurationBuilder forward(int itemNum, ItemStack itemStack){
            this.forward = new PaginationControllItem(itemNum, itemStack, PaginationControlAction.FORWARD);
            return this;
        }

        public PaginationConfigurationBuilder backward(int itemNum, Material material){
            this.backward = new PaginationControllItem(itemNum, new ItemStack(material), PaginationControlAction.BACKWARD);
            return this;
        }

        public PaginationConfigurationBuilder backward(int itemNum, ItemStack itemStack){
            this.forward = new PaginationControllItem(itemNum, itemStack, PaginationControlAction.BACKWARD);
            return this;
        }
    }

    @AllArgsConstructor
    public static class PaginationControllItem{
        @Getter private final int itemNum;
        @Getter private ItemStack itemStack;
        @Getter private PaginationControlAction controlAction;
    }

    private enum PaginationControlAction{
        BACKWARD, FORWARD;
    }
}
