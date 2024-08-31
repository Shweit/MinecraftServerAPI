package com.shweit.serverapi.commands.webHook;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.commands.SubCommand;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class DisableWebHookCommand extends SubCommand {
    @Override
    public String getName() {
        return "disable";
    }

    @Override
    public String getDescription() {
        return "Disable a specific webhook.";
    }

    @Override
    public String getSyntax() {
        return "/webhooks disable <webhook>";
    }

    @Override
    public void perform(final CommandSender commandSender, final Command command, final String label, final String[] args) {
        // Check the length of the arguments
        if (args.length != 2) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /webhooks disable <webhook>");
            return;
        }

        // Check if the webhook exists
        String webhookName = args[1];
        if (!WebHookEnum.isValid(webhookName)) {
            commandSender.sendMessage(ChatColor.RED + "Unknown webhook.");
            commandSender.sendMessage(ChatColor.RED + "Available webhooks: " + ChatColor.GREEN + WebHookEnum.getValidHooks());
            return;
        }

        // Enable the webhook
        MinecraftServerAPI.getInstance().getConfig().set("webhooks." + webhookName, false);
        MinecraftServerAPI.getInstance().saveConfig();
        commandSender.sendMessage(ChatColor.GREEN + "Webhook " + webhookName + " disabled.");
    }

    @Override
    public List<String> getSubcommandArguments(final CommandSender commandSender, final Command command, final String label, final String[] args) {
        if (args.length == 2) {
            return WebHookEnum.getValidHookList();
        }

        return null;
    }
}
