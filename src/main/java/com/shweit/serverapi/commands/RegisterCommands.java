package com.shweit.serverapi.commands;

import com.shweit.serverapi.commands.webhook.DisableWebHookCommand;
import com.shweit.serverapi.commands.webhook.EnableWebHookCommand;
import com.shweit.serverapi.commands.webhook.ListWebHookCommand;
import com.shweit.serverapi.commands.webhook.WebHookSubCommand;
import com.shweit.serverapi.utils.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class RegisterCommands {
    private JavaPlugin plugin;

    public RegisterCommands(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        CommandHandler commandHandler = new CommandHandler();

        WebHookSubCommand webHookSubCommand = new WebHookSubCommand();
        webHookSubCommand.registerSubCommand(new EnableWebHookCommand());
        Logger.debug("Registered /mcapi webhooks enable <webhook>");
        webHookSubCommand.registerSubCommand(new DisableWebHookCommand());
        Logger.debug("Registered /mcapi webhooks disable <webhook>");
        webHookSubCommand.registerSubCommand(new ListWebHookCommand());
        Logger.debug("Registered /mcapi webhooks list");
        commandHandler.register("webhooks", webHookSubCommand);

        plugin.getCommand("mcapi").setExecutor(commandHandler);
    }
}
