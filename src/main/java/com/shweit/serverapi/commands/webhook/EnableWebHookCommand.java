package com.shweit.serverapi.commands.webhook;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.commands.SubCommand;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.bukkit.command.CommandSender;

import java.util.List;

public class EnableWebHookCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        // Check the length of the arguments
        if (args.length != 1) {
            sender.sendMessage("Usage: /mcapi webhooks enable <webhook>");
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
        MinecraftServerAPI.getInstance().getConfig().set("webhooks." + webhookName, true);
        MinecraftServerAPI.getInstance().saveConfig();

        sender.sendMessage("Webhook " + webhookName + " enabled.");
    }

    @Override
    public String getPermission() {
        return "mcapi.webhook.toggle";
    }

    @Override
    public String getDescription() {
        return "Enable a specific webhook.";
    }

    @Override
    public String getUsage() {
        return "/mcapi webhooks enable <webhook>";
    }

    @Override
    public String getName() {
        return "enable";
    }

    @Override
    public List<SubCommand> getSubCommands() {
        return null;
    }
}
