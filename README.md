```xml
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
	
<dependency>
	<groupId>com.github.JaimeTruman</groupId>
	<artifactId>Bukkit-Class-Mapper</artifactId>
</dependency>
```

https://www.spigotmc.org/resources/class-mapper-api.90302/
 
```java
public class PluginMain extends JavaPlugin {
    @Override
    public void onEnable() {
        String onWrongCommand = ChatColor.DARK_RED + "Command not found";
        String onWrongPermissions = ChatColor.DARK_RED + "You dont have enough permissions";
 
        Mapper.build("com.yourpackage", this)
                .commandMapper(onWrongCommand, onWrongPermissions)
                .mobMapper()
                .eventListenerMapper()
                .startScanning();
    }
}
```
 
```java
@Command("helloworld")
public class HelloWorldCommand implements CommandRunnerNonArgs {
	@Override
	public void execute(CommandSender sender) {
        	commandSender.sendMessage("Hello " + sender.getName());
	}
}
```

```java
@Command(value = "pay", usage = {"money", "to"})
public class PayCommandRunner implements CommandRunnerArgs<PayCommand> {
	@Override
	public void execute(PayCommand command, CommandSender sender) {
        	sender.sendMessage(String.format("You paid %s %d$", command.getTo, command.getMoney));
	}
}
	
class PayCommand {
	private double money;
	private String to;
	//Getters...
}
```
	
```java
@Command(value = "pay", usage = {"money", "to"})
public class PayCommandRunner implements CommandRunnerArgs<PayCommand> {
	@Override
	public void execute(PayCommand command, CommandSender sender) {
        	sender.sendMessage(String.format("You will pay %s %d$", command.getTo.getName(), command.getMoney));
	}
}
	
class PayCommand {
	private double money;
	private Player to; //Needs to be online
	//Getters...
}
```

```java
@Command(value = "balance pay", usage = {"money", "to", " [reason]"})
public class PayCommandRunner implements CommandRunnerArgs<PayCommand> {
	@Override
	public void execute(PayCommand command, CommandSender sender) {        
		//command.getReason() can be null
        	sender.sendMessage(String.format("You will pay %s %d$", command.getTo, command.getMoney));
	}
}
```

```java
@Command(value = "balance pay", usage = {"money", "to", "[reason]¡why not!"})
public class PayCommandRunner implements CommandRunnerArgs<PayCommand> {
	@Override
	public void execute(PayCommand command, CommandSender sender) {
		//if reason not specify command.getReason() will return "why not"
        	commandSender.sendMessage(String.format("You will pay %s %d$", command.getTo, command.getMoney));
	}
}
```

```java
@Command(value = "balance pay", usage = {"money", "to", " ...[reason]¡why not!"})
public class PayCommandRunner implements CommandRunnerArgs<PayCommand> {
	@Override
	public void execute(PayCommand command, CommandSender sender) {
		//if reason not specify command.getReason() will return "why not"
		//if user types "/balance pay 10 otherplayer I love you" command.getReason() will return "I love you"
        	commandSender.sendMessage(String.format("You will pay %s %d$", command.getTo, command.getMoney));
	}
}
```

```java
@Command(value = "message", usage = {"to", "...message"})
public class PayCommandRunner implements CommandRunnerArgs<MessageCommand> {
	@Override
	public void execute(MessageCommand command, CommandSender sender) {
		//Player can do: "/message otherplayer hello bro"
	}
}
```	

```java
@Command(value = "helpme", isHelper = true, canBeTypedInConsole = true)
public final class HelpMeCommand implements CommandRunner {
	//Nothing necesary
}
```

```java
@Task(40) //It will be executed every 2 seconds
public class TestTask extends TaskRunner {
    @Override
    public void run () {
        //TODO...
    }
}
```

```java
@Task(period = 30 * BukkitTimeUnit.SECOND, delay = BukkitTimeUnit.MINUTE)
public class TestTask extends TaskRunner {
      @Override
      public void run () {
      	 //TODO...
      }
}
``` 

```java
@Mob(x = 0, y = 70, z = 0)
public class StatsMob implements OnPlayerInteractMob {
    @Override
    public void execute (PlayerInteractEntityEvent event) {
        //TODO...
    }
}
```
