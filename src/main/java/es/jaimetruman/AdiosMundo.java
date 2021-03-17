package es.jaimetruman;

import es.jaimetruman.commands.Command;
import es.jaimetruman.commands.CommandRunner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "adios")
public class AdiosMundo implements CommandRunner {
    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("adios");
    }
}
