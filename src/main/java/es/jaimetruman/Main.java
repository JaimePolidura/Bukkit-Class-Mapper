package es.jaimetruman;

import es.jaimetruman.commands.CommandMapper;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private CommandMapper commandMapper;

    @Override
    public void onEnable() {
        this.commandMapper = CommandMapper.create("es.jaimetruman","errorcaca");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
