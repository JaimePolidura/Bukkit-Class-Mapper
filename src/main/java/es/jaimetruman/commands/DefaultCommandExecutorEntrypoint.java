package es.jaimetruman.commands;

import es.jaimetruman.commands.newversion.CommandArgsObjectBuilder;
import javafx.util.Pair;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Optional;

import static org.bukkit.Bukkit.*;

public final class DefaultCommandExecutorEntrypoint implements CommandExecutor {
    private final CommandRegistry commandRegistry;
    private final CommandArgsObjectBuilder commandArgsObjectBuilder;
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
        this.commandArgsObjectBuilder = new CommandArgsObjectBuilder();
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        try{
            tryToExecuteCommand(sender, command.getName(), args);
        }catch (Exception e) {
            sender.sendMessage(ChatColor.DARK_RED + e.getMessage());
        }

        return true;
    }

    private void tryToExecuteCommand(CommandSender sender, String commandName, String[] args) throws Exception{
        validateInput(sender, commandName, args);

        CommandRunner commandRunner = getCommandRunnerInstance(commandName, args);
        Command commandInfo = getCommandData(commandName, args);

        executeCommand(commandInfo, sender, args, commandRunner);
    }

    private void validateInput(CommandSender sender, String commandName, String[] args) throws Exception{
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

    private void executeCommand(Command commandInfo, CommandSender sender, String[] args, CommandRunner commandRunner) throws Exception {
        boolean commandOfNoArgs = commandRunner instanceof CommandRunnerNonArgs;

        if(commandOfNoArgs)
            executeNonArgsCommnad(commandInfo, sender, commandRunner);
        else
            executeArgsCommand(commandInfo, sender, args, commandRunner);
    }

    private void executeNonArgsCommnad(Command commandInfo, CommandSender sender, CommandRunner commandRunner){
        CommandRunnerNonArgs commandRunnerNonArgs = (CommandRunnerNonArgs) commandRunner;

        if(commandInfo.isAsync()) {
            getScheduler().scheduleAsyncDelayedTask(plugin, () -> commandRunnerNonArgs.execute(sender), 0L);
        }else {
            commandRunnerNonArgs.execute(sender);
        }
    }

    private void executeArgsCommand(Command commandInfo, CommandSender sender, String[] args, CommandRunner commandRunner) throws Exception {
        CommandRunnerArgs commandRunnerArgs = (CommandRunnerArgs) commandRunner;
        Object objectArgs = buildObjectArgs(commandInfo, commandRunner, getActualArgsWithoutSubcommand(commandInfo, args));

        if(commandInfo.isAsync()){
            getScheduler().scheduleAsyncDelayedTask(plugin, () -> commandRunnerArgs.execute(objectArgs, sender), 0L);
        }else{
            commandRunnerArgs.execute(objectArgs, sender);
        }
    }

    private Object buildObjectArgs(Command commandInfo, CommandRunner commandRunner, String[] inputArgs) throws Exception {
        CommandRunnerArgs<Object> commandRunnerArgs = (CommandRunnerArgs) commandRunner;
        ParameterizedType paramType = (ParameterizedType) commandRunnerArgs.getClass().getGenericInterfaces()[0];
        Class<?> classObjectArg = (Class<?>) paramType.getActualTypeArguments()[0];

        return commandArgsObjectBuilder.build(commandInfo, inputArgs, classObjectArg);
    }

    private String[] getActualArgsWithoutSubcommand(Command commandInfo, String[] actualArgs){
        return commandInfo.isSubCommand() ?
                Arrays.copyOfRange(actualArgs, 1, actualArgs.length) :
                actualArgs;
    }
}
