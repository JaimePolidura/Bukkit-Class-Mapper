package es.jaimetruman.menus;

import es.jaimetruman.ClassScanner;
import es.jaimetruman.menus.eventlisteners.OnInventoryClick;
import es.jaimetruman.menus.eventlisteners.OnInventoryClose;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public final class MenuEventListenerClassMapper extends ClassScanner {
    private final Plugin plugin;

    public MenuEventListenerClassMapper(String packageToStartScanning, Plugin plugin) {
        super(packageToStartScanning);
        this.plugin = plugin;
    }

    @Override
    public void scan() {
        Bukkit.getPluginManager().registerEvents(new OnInventoryClick(), this.plugin);
        Bukkit.getPluginManager().registerEvents(new OnInventoryClose(), this.plugin);
    }
}
