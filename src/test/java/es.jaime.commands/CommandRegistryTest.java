package es.jaime.commands;

import es.jaimetruman.commands.CommandData;
import es.jaimetruman.commands.CommandRegistry;
import lombok.val;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.*;

public final class CommandRegistryTest {
    private CommandRegistry commandRegistry;

    @BeforeTest
    public void setuop(){
        this.commandRegistry = new CommandRegistry();
    }

    @Test
    public void shouldAdd(){
        this.putInRegistry("dinero", "pagar", "bolsa precio", "bolsa invertir");

        assertTrue(this.commandRegistry.findByName("dinero", new String[]{}).isPresent());
        assertTrue(this.commandRegistry.findByName("pagar", new String[]{}).isPresent());
        assertFalse(this.commandRegistry.findByName("top", new String[]{}).isPresent());
        assertTrue(this.commandRegistry.findByName("bolsa", new String[]{"precio"}).isPresent());
        assertTrue(this.commandRegistry.findByName("bolsa", new String[]{"invertir"}).isPresent());
        assertFalse(this.commandRegistry.findByName("bolsa", new String[]{"cartera"}).isPresent());

        val sucommands = this.commandRegistry.findSubcommandsByCommandName("bolsa");
        assertFalse(sucommands.isEmpty());
        assertEquals(sucommands.size(), 2);

        val mainCommands = this.commandRegistry.getMainCommands();
        assertFalse(mainCommands.isEmpty());
        assertEquals(mainCommands.size(), 3);
    }

    private void putInRegistry(String... commands){
        Arrays.stream(commands)
                .map(this::buildCommandData)
                .forEach(this.commandRegistry::put);
    }

    private CommandData buildCommandData(String value){
        return new CommandData(value, false, "", false,
                new String[]{}, null, "", "", "", false);
    }
}
