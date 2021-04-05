
# INTRODUCTION
 
This library will provide you a much faster way to develop your own plugins without the need to create complex classes or any other complex infrastructure.It will help you to declare your commands, events, mobs very easily keeping the SOLID principles.

Download: https://www.spigotmc.org/resources/class-mapper-api.90302/
 
# SETUP
 
To make this library work you have declare: Mapper.build(<base package>, <plugin instance>)
Base package. It will represent the package of your code, where it will scan for your classes
Plugin instance. The main plugin class (the one that extends JavaPlugin).
 
Now you have to declare what you want to use the plugin for. 
  * .mobMapper(). It will scan for classes that represent mobs that the player can interact with (see below Mobs).
  * .eventListenerMapper(). It will scan event listener classes and will register them for you.
  * .commandMapper(<messageOnWrongSender>, <messageOnWrongCommand>, [messageOnWrongPermission]).
    * messageOnWrongSender. The error message when the command is typed in the console.
    * messageOnWrongCommand. The error message when the command is not found.
    * messageOnWrongPermission [Optional]. The error message when the player does not have the permission.
 
Finally you have to put .startScanning() to scan all your classes.
 
```java
public class PluginMain extends JavaPlugin {
    @Override
    public void onEnable() {
        String onWrongCommand = ChatColor.DARK_RED + "Command not found";
        String onWrongSender = ChatColor.DARK_RED + "You have to be in the server";
 
        Mapper.build("es.jaime", this)
                .commandMapper(onWrongCommand, onWrongSender)
                .mobMapper()
                .eventListenerMapper()
                .startScanning();
    }
}
``` 
 
# COMMAND MAPPER

We will set up a basic helloWorld command. First we will have to declare in our plugin.yml as usual.
 
Finally we will create the class which will represent the command. This class needs these things:
 
1. Be annotated with @command. This annotation will have the basic description of your command. It includes two values.
  String name. This is required. It represents the name of the command. 
  boolean canBeTypedInConsole (Optional). If you want your command to be typed in the console you would set this to true. As default this is set to false.
  (Optional) String permissions (Optional). Not required. The permissions that the player will need to have to execute the command.

2. The class needs to implement the CommandRunner interface. This interface will have one method: void execute (CommandSender sender, String[] args). This method will be executed when the command is typed.
 
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
@Command(name = "friend list", canBeTypedInConsole = true, permissions = "bukkit.broadcast.user")
public class FriendListCommand implements CommandRunner {
	@Override
	public void execute(CommandSender commandSender, String[] strings) {
        	//TODO ...
	}
}
``` 
 
You still have to declare your commands in plugin.yml.
 
The command class (the one that is annotated and implements CommandRunner) needs to have a constructor without any parameters. Alsto the classname, fields of the class doesn't matter.
 
The commandMapper class needs to be a singleton (only one instance) which means that it is not a good idea to call CommandMapper.create(...) more than one time.
 
 
# TASK MAPPER

You can create your own task (the ones that extends BukktiRunnable) without taking care to start them. To do it, every task will represent a task. This class needs to:

1. Be annotated with @Task annotation. This will have two values
	* int period. The period when it will be executed. This is in minecraft ticks (every second = 20)
	* (Optional) int delay. When the task is loaded it will have an initial delay before running. This is set to 0 by default. This is in minecraft ticks as well.
2. Finally the class needs to implement TaskRunner interface, which will have void run() method which will be executed when the task starts.

```java
@Task(period = 40) //It will be executed every 2 seconds
public class TestTask extends TaskRunner {
   	@Override
   	public void run () {
       		//TODO...
   	}
}
```

If you want an initial delay of 1 minute and a period of 30 seconds:

```java
@Task(period = 30 * BukkitTimeUnit.SECOND, delay = BukkitTimeUnit.MINUTE)
public class TestTask extends TaskRunner {
 	@Override
    	public void run () {
        	 //TODO...
    	}
}
```
 
 
# MOB MAPPER
 
If you want a mob/entity in a fixed location that the player can interact with, you can use this part of the plugin. This is needed:
 
 1. Be annotated with  @Mob annotation. You will have to specify the x, y, z location.
 
 2. Implement the OnPlayerInteractMob, it will have this method. void execute(PlayerInteractEvent event). It will get executed when the player interacts with the mob. Example.
 
```java
@Mob(x = 0, y = 70, z = 0)
public class StatsMob implements OnPlayerInteractMob {
	@Override
	public void execute (PlayerInteractEntityEvent event) {
		//TODO...
	}
}
```
 
These classes will need to have an empty constructor. 
 
# EVENT LISTENER MAPPER
 
When you create your plugin event listener you always have to register them. Now with this library you don't need to do it. It will register them for you. 
 
The event listener classes need to have an empty constructor.
 
 
# CONTRIBUTE
 
If you want to contribute to this project feel free to pull request to here: https://github.com/JaimeTruman/Easy-Bukkit-CommandManager
