package es.jaimetruman.commands;

import es.jaimetruman.commands.exceptions.CommandNotFound;
import es.jaimetruman.commands.exceptions.InvalidUsage;
import es.jaimetruman.commands.exceptions.InvalidPermissions;
import es.jaimetruman.commands.exceptions.InvalidSenderType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

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
        if(isCommandTypeSubcommandHelp(commandName, args)){
            this.sendSubCommandHelpMessage(sender, commandName);
            return;
        }

        CommandData commandData = this.findCommand(commandName, args);
        this.ensureCorrectSenderType(sender, commandData);
        this.ensureCorrectPermissions(sender, commandData);

        if(commandData.isHelper())
            executeHelperCommand(sender);

        else if(commandData.isWithoutArgs())
            executeNonArgsCommnad(commandData, sender);

        else
            executeArgsCommand(commandData, sender, args);
    }

    private boolean isCommandTypeSubcommandHelp(String command, String[] args){
        return this.commandRegistry.findSubcommandsByCommandName(command) != null && (args.length == 0 || args[0].equals("help"));
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

    private void executeNonArgsCommnad(CommandData commandData, CommandSender sender) throws Exception{
        CommandRunnerNonArgs commandRunnerNonArgs = (CommandRunnerNonArgs) commandData.getRunner();

        if(commandData.isAsync())
            getScheduler().scheduleAsyncDelayedTask(plugin, () -> commandRunnerNonArgs.execute(sender), 0L);
        else
            commandRunnerNonArgs.execute(sender);
    }

    private void executeArgsCommand(CommandData commandData, CommandSender sender, String[] args) throws Exception{
        Object argsCommand = this.tryToBuildArgObject(commandData, getActualArgsWithoutSubcommand(commandData, args));
        CommandRunnerArgs commandRunnerArgs = (CommandRunnerArgs) commandData.getRunner();

        if(commandData.isAsync())
            getScheduler().scheduleAsyncDelayedTask(plugin, () -> commandRunnerArgs.execute(argsCommand, sender), 0L);
        else
            commandRunnerArgs.execute(argsCommand, sender);
    }

    private Object tryToBuildArgObject(CommandData commandData, String[] args) throws Exception{
        try{
            CommandRunnerArgs<Object> commandRunnerArgs = (CommandRunnerArgs) commandData.getRunner();
            ParameterizedType paramType = (ParameterizedType) commandRunnerArgs.getClass().getGenericInterfaces()[0];
            Class<?> classObjectArg = (Class<?>) paramType.getActualTypeArguments()[0];

            return commandArgsObjectBuilder.build(commandData, args, classObjectArg);
        }catch (Exception e){
            String incorrectUsageMessage =!commandData.getHelperCommand().equals("") ?
                    String.format("%s For more information %s", commandData.getUsage(), commandData.getHelperCommand()):
                    commandData.getUsage();

            throw new InvalidUsage(incorrectUsageMessage);
        }
    }

    private String[] getActualArgsWithoutSubcommand(CommandData commandInfo, String[] actualArgs) throws Exception {
        return commandInfo.isSubcommand() ?
                Arrays.copyOfRange(actualArgs, 1, actualArgs.length) :
                actualArgs;
    }

    private void sendSubCommandHelpMessage(CommandSender sender, String commandName){
        String message = this.commandRegistry.findSubcommandsByCommandName(commandName).stream()
                .map(this::buildHelpMessageForCommand)
                .collect(Collectors.joining("\n\n"));

        sender.sendMessage(message);
    }

    private void executeHelperCommand(CommandSender sender) {
        Set<CommandData> allMainCommands = this.commandRegistry.getMainCommands().stream()
                .filter(command -> !command.isHelper())
                .collect(Collectors.toSet());

        for (CommandData mainCommand : allMainCommands) {
            String commandHelp = mainCommand.isSubcommand() ?
                    String.format("%s%s", ChatColor.AQUA, mainCommand.getHelperCommand()) :
                    this.buildHelpMessageForCommand(mainCommand);

            sender.sendMessage(commandHelp + "\n\n");
        }
    }

    private String buildHelpMessageForCommand(CommandData command){
        return String.format("%s %s", ChatColor.AQUA + command.getUsage(), ChatColor.GOLD + command.getExplanation());
    }
}
