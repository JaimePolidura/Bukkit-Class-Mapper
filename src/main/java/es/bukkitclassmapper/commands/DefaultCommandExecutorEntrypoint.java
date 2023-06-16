package es.bukkitclassmapper.commands;

import es.bukkitclassmapper.ClassMapperConfiguration;
import es.bukkitclassmapper.commands.commandrunners.CommandRunnerArgs;
import es.bukkitclassmapper.commands.commandrunners.CommandRunnerNonArgs;
import es.bukkitclassmapper.commands.exceptions.CommandNotFound;
import es.bukkitclassmapper.commands.exceptions.InvalidUsage;
import es.bukkitclassmapper.commands.exceptions.InvalidPermissions;
import es.bukkitclassmapper.commands.exceptions.InvalidSenderType;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public final class DefaultCommandExecutorEntrypoint implements CommandExecutor {
    private final CommandArgsObjectBuilder commandArgsObjectBuilder;
    private final ClassMapperConfiguration configuration;
    private final CommandRegistry commandRegistry;
    private final String messageOnWrongSender;

    public DefaultCommandExecutorEntrypoint(ClassMapperConfiguration configuration) {
        this.commandRegistry = new CommandRegistry();
        this.messageOnWrongSender = "You need to be a player to execute this command";
        this.commandArgsObjectBuilder = new CommandArgsObjectBuilder();
        this.configuration = configuration;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        try{
            execute(sender, command.getName(), args);
        }catch (Exception e) {
            e.printStackTrace();

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
        this.ensureCorrectSenderType(sender);
        this.ensureCorrectPermissions(sender, commandData);

        this.getCorrespondentThreadPool(commandData).execute(() -> {
            this.executeCommand(commandData, sender, args);
        });
    }

    @SneakyThrows
    private void executeCommand(CommandData commandData, CommandSender sender, String[] args) {
        if(commandData.isHelper())
            executeHelperCommand(sender);
        else if(commandData.isWithoutArgs())
            executeNonArgsCommnad(commandData, sender);
        else
            executeArgsCommand(commandData, sender, args);
    }

    private Executor getCorrespondentThreadPool(CommandData commandData) {
        return commandData.isIO() ? this.configuration.getIOThreadPool() : this.configuration.getCommonThreadPool();
    }

    private boolean isCommandTypeSubcommandHelp(String command, String[] args){
        return this.commandRegistry.findSubcommandsByCommandName(command) != null && (args.length == 0 || args[0].equals("help"));
    }

    private void ensureCorrectSenderType(CommandSender sender){
        if(!(sender instanceof Player))
            throw new InvalidSenderType(messageOnWrongSender);
    }

    private void ensureCorrectPermissions(CommandSender sender, CommandData commandData) {
        if(!commandData.getPermissions().equals("") && !sender.hasPermission(commandData.getPermissions()))
            throw new InvalidPermissions(this.configuration.getOnWrongPermissions());
    }

    private CommandData findCommand(String commandName, String[] args){
        return commandRegistry.findByName(commandName, args)
                .orElseThrow(() -> new CommandNotFound(this.configuration.getOnCommandNotFound()));
    }

    private void executeNonArgsCommnad(CommandData commandData, CommandSender sender) {
        CommandRunnerNonArgs commandRunnerNonArgs = (CommandRunnerNonArgs) commandData.getRunner();

        commandRunnerNonArgs.execute((Player) sender);
    }

    private void executeArgsCommand(CommandData commandData, CommandSender sender, String[] args) throws Exception{
        Object argsCommand = this.tryToBuildArgObject(commandData, getActualArgsWithoutSubcommand(commandData, args));
        CommandRunnerArgs commandRunnerArgs = (CommandRunnerArgs) commandData.getRunner();

        commandRunnerArgs.execute(argsCommand, (Player) sender);
    }

    private Object tryToBuildArgObject(CommandData commandData, String[] args) {
        try{
            CommandRunnerArgs<Object> commandRunnerArgs = (CommandRunnerArgs) commandData.getRunner();
            ParameterizedType paramType = (ParameterizedType) commandRunnerArgs.getClass().getGenericInterfaces()[0];
            Class<?> classObjectArg = (Class<?>) paramType.getActualTypeArguments()[0];

            return commandArgsObjectBuilder.build(commandData, args, classObjectArg);
        }catch (Exception e){
            String incorrectUsageMessage = !commandData.getHelperCommand().equals("") ?
                    String.format("Incorrect usage: %s For more information /%s", commandData.getUsage(), commandData.getHelperCommand()):
                    String.format("Incorrect usage: %s", commandData.getUsage());

            incorrectUsageMessage = incorrectUsageMessage.equalsIgnoreCase("Player needs to be online") ?
                    e.getMessage() :
                    incorrectUsageMessage;

            throw new InvalidUsage(incorrectUsageMessage);
        }
    }

    private String[] getActualArgsWithoutSubcommand(CommandData commandInfo, String[] actualArgs) {
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
                    String.format("%s/%s help", ChatColor.AQUA, mainCommand.getCommand()) :
                    this.buildHelpMessageForCommand(mainCommand);

            sender.sendMessage(commandHelp);
            sender.sendMessage(" ");
        }
    }

    private String buildHelpMessageForCommand(CommandData command){
        return String.format("%s %s", ChatColor.AQUA + command.getUsage(), ChatColor.GOLD + command.getExplanation());
    }
}
