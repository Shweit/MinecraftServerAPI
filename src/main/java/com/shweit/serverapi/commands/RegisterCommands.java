package com.shweit.serverapi.commands;

import com.shweit.serverapi.commands.version.VersionCommand;
import com.shweit.serverapi.commands.webHook.DisableWebHookCommand;
import com.shweit.serverapi.commands.webHook.EnableWebHookCommand;
import com.shweit.serverapi.commands.webHook.SendWebHookCommand;
import com.shweit.serverapi.commands.webHook.ListWebHook;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class RegisterCommands {
    private JavaPlugin plugin;

    public RegisterCommands(final JavaPlugin javaPlugin) {
        this.plugin = javaPlugin;
    }

    public void register() {
        List<SubCommand> webHookSubCommands = List.of(
            new ListWebHook(),
            new EnableWebHookCommand(),
            new DisableWebHookCommand(),
            new SendWebHookCommand()
        );
        CommandManager webhookCommandManager = new CommandManager(webHookSubCommands);
        plugin.getCommand("webhooks").setExecutor(webhookCommandManager);


        List<SubCommand> mcapiSubCommands = List.of(
            new VersionCommand()
        );
        CommandManager versionCommandManager = new CommandManager(mcapiSubCommands);
        plugin.getCommand("mcapi").setExecutor(versionCommandManager);
    }
}
