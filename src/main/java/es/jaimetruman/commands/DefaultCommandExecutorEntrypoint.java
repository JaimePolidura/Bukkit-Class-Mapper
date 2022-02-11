package es.jaimetruman.commands;

import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

public final class DefaultCommandExecutorEntrypoint implements CommandExecutor {
    private final CommandRegistry commandRegistry;
    private final String messageOnWrongSender;
    private final String messageOnCommandNotFound;
    private final String messageOnNotHavePermissions;
    private final Plugin plugin;

    public DefaultCommandExecutorEntrypoint(CommandRegistry commandRegistry, String messageOnWrongSender, String messageOnCommandNotFound,
                                            String messageOnNotHavePermissions, Plugin plugin) {
        this.commandRegistry = commandRegistry;
        this.messageOnWrongSender = messageOnWrongSender;
        this.messageOnCommandNotFound = messageOnCommandNotFound;
        this.messageOnNotHavePermissions = messageOnNotHavePermissions;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        try{
            tryToExecuteCommand(sender, command.getName(), args);
        }catch (Exception e) {
            sender.sendMessage(e.getMessage());
        }finally {
            return true;
        }
    }

    private void tryToExecuteCommand(CommandSender sender, String commandName, String[] args) throws Exception{
        validateOrThrowException(sender, commandName, args);

        CommandRunner commandRunner = getCommandRunnerInstance(commandName, args);
        Command commandInfo = getCommandData(commandName, args);

        if(commandInfo.isAsync()) {
            Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, () -> commandRunner.execute(sender, args), 0L);
        }else{
            commandRunner.execute(sender, args);
        }
    }

    private void validateOrThrowException(CommandSender sender, String commandName, String[] args) throws Exception{
        Optional<Pair<CommandRunner, Command>> optionalCommandRunner = commandRegistry.findByName(commandName, args);

        if(!optionalCommandRunner.isPresent()){
            throw new Exception(messageOnCommandNotFound);
        }

        Command commandData = optionalCommandRunner.get().getValue();

        if(!(sender instanceof Player) && !commandData.canBeTypedInConsole()){
            throw new Exception(messageOnWrongSender);
        }
        if(!commandData.permissions().equals("") && !sender.hasPermission(commandData.permissions())){
            throw new Exception(messageOnNotHavePermissions);
        }
    }

    private Command getCommandData(String commandName, String[] args){
        return this.commandRegistry.findByName(commandName, args).get().getValue();
    }

    private CommandRunner getCommandRunnerInstance(String commandName, String[] args){
        return this.commandRegistry.findByName(commandName, args).get().getKey();
    }
}
