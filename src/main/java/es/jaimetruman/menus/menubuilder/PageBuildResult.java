package es.jaimetruman.menus.menubuilder;

import es.jaimetruman.menus.Page;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Queue;

@AllArgsConstructor
public final class PageBuildResult {
    @Getter private final Page page;
    @Getter private final Queue<ItemStack> overflow;
}
