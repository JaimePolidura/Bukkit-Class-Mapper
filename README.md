
# INTRODUCTION
 
This library will provide you a much faster way to develop your own plugins without the need to create complex classes or any other complex infrastructure.It will help you to declare your commands, events, mobs very easily keeping the SOLID principles.
 
# SETUP
 
Download: https://www.spigotmc.org/resources/class-mapper-api.90302/
 
To make this library work you have declare: Mapper.build(<base package>, <plugin instance>)
Base package. It will represent the package of your code, where it will scan for your classes
Plugin instance. The main plugin class (the one that extends JavaPlugin). 

All classes needs to have a public empty constructor 

And then you have specify which options you want to use in your plugin.
 
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

You still have to declare your commands in plugin.yml.

 
```java
@Command("helloworld")
public class HelloWorldCommand implements CommandRunner {
	@Override
	public void execute(CommandSender sender, String[] args) {
        	commandSender.sendMessage("Hello " + sender.getName());
	}
}
```
 
If you a subcommand that can be typed in the console, with permissions and runned in other thread:
 
```java
@Command(value = "friend list", canBeTypedInConsole = true, permissions = "bukkit.broadcast.user", asynch = true)
public class FriendListCommand implements CommandRunner {
    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        //TODO ...
    }
}
``` 
 
# TASK MAPPER

You can create your own task (the ones that extends BukktiRunnable) without taking care to start them. The time will be in ticks (every 20 ticks it is 1 second)

```java
@Task(40) //It will be executed every 2 seconds
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
 
If you want a mob/entity in a fixed location that the player can interact with, you can use this part of the plugin. 

```java
@Mob(x = 0, y = 70, z = 0)
public class StatsMob implements OnPlayerInteractMob {
    @Override
    public void execute (PlayerInteractEntityEvent event) {
        //TODO...
    }
}
```
 
# EVENT LISTENER MAPPER
 
When you create your plugin event listener you always have to register them. Now with this library you don't need to do it. It will register them for you. 
 
The event listener classes need to have an empty constructor.
 
 
# CONTRIBUTE
 
If you want to contribute to this project feel free to pull request to here: https://github.com/JaimeTruman/Easy-Bukkit-CommandManager
