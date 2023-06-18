package es.bukkitclassmapper.commands;

import es.bukkitclassmapper.ClassMapperConfiguration;
import es.bukkitclassmapper._shared.utils.FakeExecutor;
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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public final class DefaultCommandExecutorEntrypoint implements CommandExecutor {
    private final CommandArgsObjectBuilder commandArgsObjectBuilder;
    private final ClassMapperConfiguration configuration;
    private final CommandRegistry commandRegistry;
    private final String messageOnWrongSender;

    private final FakeExecutor fakeExecutor;

    public DefaultCommandExecutorEntrypoint(ClassMapperConfiguration configuration, CommandRegistry commandRegistry) {
        this.commandArgsObjectBuilder = new CommandArgsObjectBuilder();
        this.messageOnWrongSender = "You need to be a player to execute this command";
        this.commandRegistry = commandRegistry;
        this.fakeExecutor = new FakeExecutor();
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

    private void execute(CommandSender sender, String commandName, String[] args) {
        boolean isSubcommand = isSubcommand(commandName, args);
        boolean isHelper = isHelper(commandName, args);

        if(isSubcommand && isHelper){
            sendSubCommandHelpMessage(sender, commandName);
        }else if(!isSubcommand && isHelper) {
            sendMainCommandsHelpMessage(sender);
        }else{
            validateArgsCommandAndExecute(sender, commandName, args);
        }
    }

    private void validateArgsCommandAndExecute(CommandSender sender, String commandName, String[] args) {
        CommandData commandData = findCommand(commandName, args);
        ensureCorrectSenderType(sender);
        ensureCorrectPermissions(sender, commandData);

        getCorrespondentThreadPool(commandData).execute(() -> {
            executeCommand(commandData, sender, args);
        });
    }

    @SneakyThrows
    private void executeCommand(CommandData commandData, CommandSender sender, String[] args) {
        if(commandData.isWithoutArgs())
            executeNonArgsCommnad(commandData, sender);
        else
            executeArgsCommand(commandData, sender, args);
    }

    private void executeNonArgsCommnad(CommandData commandData, CommandSender sender) {
        CommandRunnerNonArgs commandRunnerNonArgs = (CommandRunnerNonArgs) commandData.getRunner();

        commandRunnerNonArgs.execute((Player) sender);
    }

    private void executeArgsCommand(CommandData commandData, CommandSender sender, String[] args) {
        Object argsCommand = this.tryToBuildArgObject(commandData, getActualArgsWithoutSubcommand(commandData, args));
        CommandRunnerArgs commandRunnerArgs = (CommandRunnerArgs) commandData.getRunner();

        commandRunnerArgs.execute(argsCommand, (Player) sender);
    }

    private Object tryToBuildArgObject(CommandData commandData, String[] args) {
        try{
            CommandRunnerArgs<Object> commandRunnerArgs = (CommandRunnerArgs<Object>) commandData.getRunner();
            ParameterizedType paramType = (ParameterizedType) commandRunnerArgs.getClass().getGenericInterfaces()[0];
            Class<?> classObjectArg = (Class<?>) paramType.getActualTypeArguments()[0];

            return commandArgsObjectBuilder.build(commandData, args, classObjectArg);

        }catch (Exception e){
            String incorrectUsageMessage = !e.getMessage().equalsIgnoreCase("Player needs to be online") ?
                    String.format("Incorrect usage: %s", commandData.getUsage()) :
                    e.getMessage();

            throw new InvalidUsage(incorrectUsageMessage);
        }
    }

    private boolean isHelper(String commandName, String[] args) {
        Optional<CommandData> mainCommand = commandRegistry.findMainCommandByName(commandName);
        Optional<CommandData> subCommand = commandRegistry.findSubcommandByMainCommandName(commandName, args);
        
        return (mainCommand.isPresent() && mainCommand.get().isHelper()) ||
                (subCommand.isPresent() && subCommand.get().isHelper());
    }

    private boolean isSubcommand(String commandName, String[] args) {
        Optional<CommandData> commandOptional = commandRegistry.findByName(commandName, args);

        return args.length > 0 && commandOptional.isPresent() && commandOptional.get().isSubcommand();
    }

    private Executor getCorrespondentThreadPool(CommandData commandData) {
        return commandData.isAsync() ? this.configuration.getThreadPool() : fakeExecutor;
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

    private String[] getActualArgsWithoutSubcommand(CommandData commandInfo, String[] actualArgs) {
        return commandInfo.isSubcommand() ?
                Arrays.copyOfRange(actualArgs, 1, actualArgs.length) :
                actualArgs;
    }

    private void sendSubCommandHelpMessage(CommandSender sender, String commandName){
        String message = this.commandRegistry.findSubcommandsByCommandName(commandName).stream()
                .filter(commandData -> !commandData.isHelper())
                .map(this::buildHelpMessageForCommand)
                .collect(Collectors.joining("\n\n"));

        sender.sendMessage(message);
    }

    private void sendMainCommandsHelpMessage(CommandSender sender) {
        Set<CommandData> allMainCommands = this.commandRegistry.getMainCommands().stream()
                .filter(command -> !command.isHelper())
                .collect(Collectors.toSet());

        for (CommandData mainCommand : allMainCommands) {
            sender.sendMessage(buildHelpMessageForCommand(mainCommand));
            sender.sendMessage(" ");
        }
    }

    private String buildHelpMessageForCommand(CommandData command){
        return String.format("%s %s", ChatColor.AQUA + command.getUsage(), ChatColor.GOLD + command.getExplanation());
    }
}
