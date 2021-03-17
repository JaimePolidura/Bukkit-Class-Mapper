package es.jaimetruman;

import es.jaimetruman.commands.Command;
import es.jaimetruman.commands.CommandRunner;
import org.bukkit.command.CommandSender;

@Command(name = "bolsa cartera")
public class PruebaSubComando2 implements CommandRunner {
    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("bolsa cartera");
    }
}
