package com.shweit.serverapi.commands;

import com.shweit.serverapi.commands.webHook.DisableWebHookCommand;
import com.shweit.serverapi.commands.webHook.EnableWebHookCommand;
import com.shweit.serverapi.commands.webHook.ListWebHook;
import com.shweit.serverapi.utils.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class RegisterCommands {
    private JavaPlugin plugin;

    public RegisterCommands(JavaPlugin plugin) {
        this.plugin = plugin;
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
