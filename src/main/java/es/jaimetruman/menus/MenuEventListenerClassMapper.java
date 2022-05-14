package es.jaimetruman.menus;

import es.jaimetruman.ClassScanner;
import org.bukkit.plugin.Plugin;

public final class MenuEventListenerClassMapper extends ClassScanner {
    private final Plugin plugin;

    public MenuEventListenerClassMapper(String packageToStartScanning, Plugin plugin) {
        super(packageToStartScanning);
        this.plugin = plugin;
    }

    @Override
    public void scan() {
    }
}
