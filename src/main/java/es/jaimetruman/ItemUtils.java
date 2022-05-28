package es.jaimetruman;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class ItemUtils {
    public static String getLore(ItemStack item, int index){
        String rawString = item.getItemMeta().getLore().get(index);

        return rawString.replaceAll("§c", "")
                .replaceAll("§l", "");
    }

    public static ItemStack setLore(ItemStack item, int index, String newLoreLine){
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore = itemMeta.getLore();
        lore.set(index, newLoreLine);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }
}
