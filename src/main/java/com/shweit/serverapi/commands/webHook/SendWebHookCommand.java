package com.shweit.serverapi.commands.webHook;

import com.shweit.serverapi.commands.SubCommand;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.json.JSONObject;

import java.util.List;

public final class SendWebHookCommand extends SubCommand {
    @Override
    public String getName() {
        return "send";
    }

    @Override
    public String getDescription() {
        return "Send a webhook with a specific event.";
    }

    @Override
    public String getSyntax() {
        return "/webhooks send <event>";
    }

    @Override
    public void perform(final CommandSender commandSender, final Command command, final String label, final String[] args) {
        if (args.length != 2) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /webhooks send <event>");
            return;
        }

        String event = args[1];
        JSONObject json = new JSONObject();
        json.put("event", event);

        // Send the webhook
        RegisterWebHooks.sendToAllUrls(json);

        commandSender.sendMessage(ChatColor.GREEN + "Webhook " + event + " sent.");
    }

    @Override
    public List<String> getSubcommandArguments(final CommandSender commandSender, final Command command, final String label, final String[] args) {
        return List.of();
    }
}
