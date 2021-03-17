package es.jaimetruman;

import es.jaimetruman.commands.Command;
import es.jaimetruman.commands.CommandRunner;
import org.bukkit.command.CommandSender;

@Command(name = "hola", canBeTypedInConsole = true)
public class HolaComando implements CommandRunner {
    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("hola");
    }
}
