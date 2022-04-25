package es.jaime.commands;

import es.jaimetruman.commands.CommandArgsObjectBuilder;
import es.jaimetruman.commands.CommandData;
import lombok.Getter;
import lombok.SneakyThrows;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.testng.Assert.*;

public class CommandArgsObjectBuilderTest {
    private final CommandArgsObjectBuilder commandArgsObjectBuilder;

    public CommandArgsObjectBuilderTest (){
        this.commandArgsObjectBuilder = new CommandArgsObjectBuilder();
    }

    @Test
    @SneakyThrows
    public void simpleCommandArgs(){
        CommandData commandData = this.getCommandInfoAnnotation(new String[]{"id", "who", "price"});
        UUID uuid = UUID.randomUUID();
        String[] actualArgs = new String[]{uuid.toString(), "jaime", "12.2"};

        SimpleCommand simpleCommandArgsObject = this.commandArgsObjectBuilder.build(commandData, actualArgs,
                SimpleCommand.class);

        assertEquals(simpleCommandArgsObject.getWho(), "jaime");
        assertEquals(simpleCommandArgsObject.getPrice(), 12.2, 0D);
        assertEquals(simpleCommandArgsObject.getId().toString(), uuid.toString());
    }

    @Test
    @SneakyThrows
    public void optionalCommandArg(){
        CommandData commandData = this.getCommandInfoAnnotation(new String[]{"player", "[reason]"});
        OptionalArgCommand commandObject1 = this.commandArgsObjectBuilder.build(commandData, new String[]{"jaime"},
                OptionalArgCommand.class);
        assertEquals(commandObject1.getPlayer(), "jaime");
        assertNull(commandObject1.getReason());

        OptionalArgCommand commandObject2 = this.commandArgsObjectBuilder.build(commandData, new String[]{"jaime", "value"},
                OptionalArgCommand.class);
        assertEquals(commandObject2.getPlayer(), "jaime");
        assertEquals(commandObject2.getReason(), "value");
    }

    @Test
    @SneakyThrows
    public void optionalCommandArgWithDefaultValue(){
        CommandData commandData = this.getCommandInfoAnnotation(new String[]{"player", "[reason]¡pordefecto!"});
        OptionalArgCommand commandObject1 = this.commandArgsObjectBuilder.build(commandData, new String[]{"jaime"},
                OptionalArgCommand.class);
        assertEquals(commandObject1.getPlayer(), "jaime");
        assertEquals(commandObject1.getReason(), "pordefecto");

        OptionalArgCommand commandObject2 = this.commandArgsObjectBuilder.build(commandData, new String[]{"jaime", "value"},
                OptionalArgCommand.class);
        assertEquals(commandObject2.getPlayer(), "jaime");
        assertEquals(commandObject2.getReason(), "value");
    }

    @Test
    @SneakyThrows
    public void textCommandArg(){
        CommandData commandData = this.getCommandInfoAnnotation(new String[]{"player", "...description"});
        TextArgCommand commandObject1 = this.commandArgsObjectBuilder.build(commandData, new String[]{"jaime", "descripccion"},
                TextArgCommand.class);
        assertEquals(commandObject1.getPlayer(), "jaime");
        assertEquals(commandObject1.getDescription(), "descripccion");

        TextArgCommand commandObject2 = this.commandArgsObjectBuilder.build(commandData, new String[]{"jaime", "descripccion de la hostia"},
                TextArgCommand.class);
        assertEquals(commandObject2.getPlayer(), "jaime");
        assertEquals(commandObject2.getDescription(), "descripccion de la hostia");
    }

    @Test
    @SneakyThrows
    public void optionalTextCommandArg(){
        CommandData commandData = this.getCommandInfoAnnotation(new String[]{"player", "...[description]"});
        TextArgCommand commandObject1 = this.commandArgsObjectBuilder.build(commandData, new String[]{"jaime"},
                TextArgCommand.class);
        assertEquals(commandObject1.getPlayer(), "jaime");
        assertNull(commandObject1.getDescription());

        TextArgCommand commandObject2 = this.commandArgsObjectBuilder.build(commandData, new String[]{"jaime", "descripccion de la hostia"},
                TextArgCommand.class);
        assertEquals(commandObject2.getPlayer(), "jaime");
        assertEquals(commandObject2.getDescription(), "descripccion de la hostia");
    }

    @Test
    @SneakyThrows
    public void optionalTextWidthDefaultOptionCommandArg(){
        CommandData commandData = this.getCommandInfoAnnotation(new String[]{"player", "...[description]¡pordefecto!"});
        TextArgCommand commandObject1 = this.commandArgsObjectBuilder.build(commandData, new String[]{"jaime"},
                TextArgCommand.class);
        assertEquals(commandObject1.getPlayer(), "jaime");
        assertEquals(commandObject1.getDescription(), "pordefecto");

        TextArgCommand commandObject2 = this.commandArgsObjectBuilder.build(commandData, new String[]{"jaime", "descripccion de la hostia"},
                TextArgCommand.class);
        assertEquals(commandObject2.getPlayer(), "jaime");
        assertEquals(commandObject2.getDescription(), "descripccion de la hostia");
    }

    private CommandData getCommandInfoAnnotation(String[] args){
        return new CommandData(
                "hola",true, "", false, args, null,
                "", "", "explanation", false);
    }

    public static class SimpleCommand {
        @Getter private UUID id;
        @Getter private String who;
        @Getter private double price;
    }

    public static class OptionalArgCommand {
        @Getter private String player;
        @Getter private String reason;
    }

    public static class TextArgCommand {
        @Getter private String player;
        @Getter private String description;
    }
}
