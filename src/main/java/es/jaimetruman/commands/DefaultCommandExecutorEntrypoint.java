package es.jaimetruman.commands;

import es.jaimetruman.commands.exceptions.CommandNotFound;
import es.jaimetruman.commands.exceptions.InvalidPermissions;
import es.jaimetruman.commands.exceptions.InvalidSenderType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

import static org.bukkit.Bukkit.*;

public final class DefaultCommandExecutorEntrypoint implements CommandExecutor {
    private final CommandRegistry commandRegistry;
    private final CommandArgsObjectBuilder commandArgsObjectBuilder;
    private final String messageOnWrongSender;
    private final String messageOnCommandNotFound;
    private final String messageOnNotHavePermissions;
    private final Plugin plugin;

    public DefaultCommandExecutorEntrypoint(CommandRegistry commandRegistry, String messageOnCommandNotFound,
                                            Plugin plugin, String messageOnNotHavingPermissinos) {
        this.commandRegistry = commandRegistry;
        this.messageOnWrongSender = "You need to be a player to execute this command";
        this.messageOnNotHavePermissions = messageOnNotHavingPermissinos;
        this.messageOnCommandNotFound = messageOnCommandNotFound;
        this.plugin = plugin;
        this.commandArgsObjectBuilder = new CommandArgsObjectBuilder();
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        try{
            execute(sender, command.getName(), args);
        }catch (Exception e) {
            sender.sendMessage(ChatColor.DARK_RED + e.getMessage());
        }

        return true;
    }

    private void execute(CommandSender sender, String commandName, String[] args) throws Exception{
        CommandData commandData = this.findCommand(commandName, args);
        this.ensureCorrectSenderType(sender, commandData);
        this.ensureCorrectPermissions(sender, commandData);

        if(commandData.isWithoutArgs())
            executeNonArgsCommnad(commandData, sender);
        else
            executeArgsCommand(commandData, sender, args);
    }

    private void ensureCorrectSenderType(CommandSender sender, CommandData commandData){
        if(!(sender instanceof Player) && !commandData.canBeTypedInConsole())
            throw new InvalidSenderType(messageOnWrongSender);
    }

    private void ensureCorrectPermissions(CommandSender sender, CommandData commandData) {
        if(!commandData.getPermissions().equals("") && !sender.hasPermission(commandData.getPermissions()))
            throw new InvalidPermissions(messageOnNotHavePermissions);
    }

    private CommandData findCommand(String commandName, String[] args){
        return commandRegistry.findByName(commandName, args)
                .orElseThrow(() -> new CommandNotFound(messageOnCommandNotFound));
    }

    private void executeNonArgsCommnad(CommandData commandData, CommandSender sender){
        CommandRunnerNonArgs commandRunnerNonArgs = (CommandRunnerNonArgs) commandData.getRunner();

        if(commandData.isAsync())
            getScheduler().scheduleAsyncDelayedTask(plugin, () -> commandRunnerNonArgs.execute(sender), 0L);
        else
            commandRunnerNonArgs.execute(sender);
    }

    private void executeArgsCommand(CommandData commandData, CommandSender sender, String[] args) throws Exception {
        CommandRunnerArgs commandRunnerArgs = (CommandRunnerArgs) commandData.getRunner();
        Object objectArgs = buildObjectArgs(commandData, getActualArgsWithoutSubcommand(commandData, args));

        if(commandData.isAsync())
            getScheduler().scheduleAsyncDelayedTask(plugin, () -> commandRunnerArgs.execute(objectArgs, sender), 0L);
        else
            commandRunnerArgs.execute(objectArgs, sender);
    }

    private Object buildObjectArgs(CommandData commandData, String[] inputArgs) throws Exception {
        CommandRunnerArgs<Object> commandRunnerArgs = (CommandRunnerArgs) commandData.getRunner();
        ParameterizedType paramType = (ParameterizedType) commandRunnerArgs.getClass().getGenericInterfaces()[0];
        Class<?> classObjectArg = (Class<?>) paramType.getActualTypeArguments()[0];

        return commandArgsObjectBuilder.build(commandData, inputArgs, classObjectArg);
    }

    private String[] getActualArgsWithoutSubcommand(CommandData commandInfo, String[] actualArgs){
        return commandInfo.isSubcommand() ?
                Arrays.copyOfRange(actualArgs, 1, actualArgs.length) :
                actualArgs;
    }
}
