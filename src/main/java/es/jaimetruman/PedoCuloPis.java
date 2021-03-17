package es.jaimetruman;

import es.jaimetruman.commands.Command;
import es.jaimetruman.commands.CommandRunner;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@Command(name = "caca pedo")
public class PedoCuloPis implements CommandRunner {
    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.DARK_AQUA + "caca pedo");
    }
}
