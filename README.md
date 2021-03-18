
#INTRODUCTION

This library will provide you an effective way to create your own commands in a really clean way without the need to create complex commandManager classes or any other complex infrastructure.

This library will help you to declare your commands very easily keeping the SOLID principles.

Download jar: https://www.spigotmc.org/resources/easy-command-manager.90302/

#DOCUMENTATION

##YAML

We will set up a basic helloWorld command. First we will have to declare in our plugin.yml as usual.

```yaml
commands:
  helloworld:
    description: a test command
    usage: /<command>[/code]
```

##COMMAND MAPPER

Then we have to tell the library where to start scanning the classes and what messages will be sent to the sender if something went wrong. To do that, in the method onEnable of the main class of the plugin, we will put this line: CommandMapper.create(String packageToStartScanning, String messageOnWrongCommand, String messageOnWrongSender). Where:
String packageToStartScanning. This will represent your packages where your commands will be.

String messageOnWrongCommand. It represents what message will be sent to the player if the command is not found.

String messageOnWrongSender. The message that will be sent to the user in the console if the command cannot be typed in the console. (see [USER=363717]@command[/USER])

```java
public class Main extends JavaPlugin {
    private CommandMapper mapper;

    @Override
    public void onEnable() {
        mapper = CommandMapper.create("es.jaime",
                "Command not found type /help",
                "You need to be logged to type this command");
    }
}
```

##COMMAND CLASS

Finally we will create the class which will represent the command. This class needs these things:

1º)  Be annotated with [USER=363717]@command[/USER]. This annotation will have the basic description of your command. It includes two values.
String name. This is required. It represents the name of the command. If you want your class to represent subcommands this will be as easy as do this @Command(name = “friend list”) main command: friend, subcommand: list

boolean canBeTypedInConsole. This is not required. If you want your command to be typed in the console you would set this to true. As default this is set to false.

2º)  The class needs to implement CommandRunner interface. This interface will have one method: void execute (CommandSender sender, String[] args). This method will be executed when the command is typed.

```java
@Command(name = "helloworld")
public class HelloWorldCommand implements CommandRunner {
 
    @Override
    public void execute(CommandSender sender, String[] args) {
        commandSender.sendMessage("Hello " + sender.getName());
    }
}
```

If you a subcommand that can be typed in the console you would something like this:

```java
@Command(name = "friend list", canBeTypedInConsole = true)
public class FriendListCommand implements CommandRunner {
 
    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        //TODO ...
    }
}
```

#IMPORTANT THINGS

You still have to declare your commands in plugin.yml.

The command class (the one that is annotated and implements CommandRunner) needs to have a constructor without any parameters. Alsto the classname, fields of the class doesnt matter.

The commandMapper class needs to be a singleton (only one instance) which means that it is not a good idea to call CommandMapper.create(...) more than one time.
