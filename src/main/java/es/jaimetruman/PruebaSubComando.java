package es.jaimetruman;

import es.jaimetruman.commands.Command;
import es.jaimetruman.commands.CommandRunner;
import org.bukkit.command.CommandSender;

@Command(name = "bolsa invertir")
public class PruebaSubComando implements CommandRunner {
    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("PruebaSubComando");
    }
}
