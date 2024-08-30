package com.shweit.serverapi.commands.webHook;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.commands.SubCommand;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class EnableWebHookCommand extends SubCommand {
    @Override
    public String getName() {
        return "enable";
    }

    @Override
    public String getDescription() {
        return "Enable a specific webhook.";
    }

    @Override
    public String getSyntax() {
        return "/webhooks enable <webhook>";
    }

    @Override
    public void perform(CommandSender commandSender, Command command, String label, String[] args) {
        // Check the length of the arguments
        if (args.length != 2) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /webhooks enable <webhook>");
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
        MinecraftServerAPI.getInstance().getConfig().set("webhooks." + webhookName, true);
        MinecraftServerAPI.getInstance().saveConfig();
        commandSender.sendMessage(ChatColor.GREEN + "Webhook " + webhookName + " enabled.");
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 2) {
            return WebHookEnum.getValidHookList();
        }

        return null;
    }
}
