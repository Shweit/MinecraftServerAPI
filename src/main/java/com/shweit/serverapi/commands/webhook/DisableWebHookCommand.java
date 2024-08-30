package com.shweit.serverapi.commands.webhook;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.commands.SubCommand;
import com.shweit.serverapi.utils.Logger;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class DisableWebHookCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        // Check the length of the arguments
        if (args.length != 1) {
            sender.sendMessage("Usage: /mcapi webhooks disable <webhook>");
            return;
        }

        // Check if the webhook exists
        String webhookName = args[0];
        if (!WebHookEnum.isValid(webhookName)) {
            sender.sendMessage("Unknown webhook.");
            sender.sendMessage("Available webhooks: " + WebHookEnum.getValidHooks());
            return;
        }

        // Enable the webhook
        WebHookEnum webhook = WebHookEnum.valueOf(webhookName.toUpperCase());
        MinecraftServerAPI.getInstance().getConfig().set("webhooks." + webhookName, false);
        MinecraftServerAPI.getInstance().saveConfig();

        sender.sendMessage("Webhook " + webhookName + " enabled.");
    }

    @Override
    public String getPermission() {
        return "mcapi.webhook.toggle";
    }

    @Override
    public String getDescription() {
        return "Disable a specific webhook.";
    }

    @Override
    public String getUsage() {
        return "/mcapi webhooks disable <webhook>";
    }

    @Override
    public String getName() {
        return "disable";
    }

    @Override
    public List<SubCommand> getSubCommands() {
        return null;
    }
}
