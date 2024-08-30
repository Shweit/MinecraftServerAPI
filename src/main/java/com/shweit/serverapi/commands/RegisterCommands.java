package com.shweit.serverapi.commands;

import com.shweit.serverapi.commands.webHook.DisableWebHookCommand;
import com.shweit.serverapi.commands.webHook.EnableWebHookCommand;
import com.shweit.serverapi.commands.webHook.ListWebHook;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class RegisterCommands {
    private JavaPlugin plugin;

    public RegisterCommands(final JavaPlugin javaPlugin) {
        this.plugin = javaPlugin;
    }

    public void register() {
        List<SubCommand> subCommands = List.of(
            new ListWebHook(),
            new EnableWebHookCommand(),
            new DisableWebHookCommand()
        );

        CommandManager webhookCommandManager = new CommandManager(subCommands);
        plugin.getCommand("webhooks").setExecutor(webhookCommandManager);
    }
}
