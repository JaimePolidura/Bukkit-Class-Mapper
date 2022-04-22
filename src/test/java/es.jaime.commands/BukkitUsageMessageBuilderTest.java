package es.jaime.commands;

import es.jaimetruman.commands.BukkitUsageMessageBuilder;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public final class BukkitUsageMessageBuilderTest {
    private final BukkitUsageMessageBuilder bukkitUsageMessageBuilder;

    public BukkitUsageMessageBuilderTest(){
        this.bukkitUsageMessageBuilder = new BukkitUsageMessageBuilder();
    }

    @Test
    public void nonArgs(){
        String usage = this.bukkitUsageMessageBuilder.build("balance", new String[]{""});
        assertEquals(usage, "/balance ");

        String usage2 = this.bukkitUsageMessageBuilder.build("money balance", new String[]{""});
        assertEquals(usage2, "/money balance ");
    }

    @Test
    public void simpleArgs(){
        String usage1 = this.bukkitUsageMessageBuilder.build("balance", new String[]{"player"});
        assertEquals(usage1, "/balance <player>");

        String usage2 = this.bukkitUsageMessageBuilder.build("pay", new String[]{"player", "money"});
        assertEquals(usage2, "/pay <player> <money>");

        String usage3 = this.bukkitUsageMessageBuilder.build("money balance", new String[]{"player"});
        assertEquals(usage3, "/money balance <player>");

        String usage4 = this.bukkitUsageMessageBuilder.build("money pay", new String[]{"player", "money"});
        assertEquals(usage4, "/money pay <player> <money>");
    }

    @Test
    public void complexArgs(){
        String usage1 = this.bukkitUsageMessageBuilder.build("fire", new String[]{"player", "[reason]"});
        assertEquals(usage1, "/fire <player> [reason]");

        String usage2 = this.bukkitUsageMessageBuilder.build("fire", new String[]{"player", "...[reason]"});
        assertEquals(usage2, "/fire <player> ...[reason]");

        String usage3 = this.bukkitUsageMessageBuilder.build("fire", new String[]{"player", "...[reason]¡pordefecto!"});
        assertEquals(usage3, "/fire <player> ...[reason]");

        String usage4 = this.bukkitUsageMessageBuilder.build("employees fire", new String[]{"player", "...[reason]¡pordefecto!"});
        assertEquals(usage4, "/employees fire <player> ...[reason]");
    }
}
