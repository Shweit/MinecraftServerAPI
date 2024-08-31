package com.shweit.serverapi.commands.webHook;

import com.shweit.serverapi.commands.SubCommand;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public final class ListWebHook extends SubCommand {
    private static final int ENTRIES_PER_PAGE = 7;

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "List all webhooks.";
    }

    @Override
    public String getSyntax() {
        return "/webhooks list <page>";
    }

    @Override
    public void perform(final CommandSender commandSender, final Command command, final String label, final String[] args) {
        int page = 1;

        if (args.length == 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(ChatColor.RED + "Invalid page number!");
                return;
            }
        }

        int totalPages = (int) Math.ceil((double) WebHookEnum.values().length / ENTRIES_PER_PAGE);

        if (page > totalPages || page <= 0) {
            commandSender.sendMessage(ChatColor.RED + "Invalid page number! Please enter a number between 1 and " + totalPages);
            return;
        }

        commandSender.sendMessage(
            ChatColor.GRAY + "----------------["
            + ChatColor.LIGHT_PURPLE + " WebHooks - Page " + page + "/" + totalPages + ChatColor.GRAY
            + " ]----------------"
        );
        commandSender.sendMessage("");
        commandSender.sendMessage(ChatColor.WHITE + "Registered WebHooks (" + WebHookEnum.values().length + "):");
        commandSender.sendMessage("");
        commandSender.sendMessage(ChatColor.GOLD + "Available WebHooks:");

        int startIndex = (page - 1) * ENTRIES_PER_PAGE;
        int endIndex = Math.min(startIndex + ENTRIES_PER_PAGE, WebHookEnum.values().length);

        for (int i = startIndex; i < endIndex; i++) {
            WebHookEnum webhook = WebHookEnum.values()[i];
            ChatColor color = RegisterWebHooks.doActivateWebhook(webhook.label) ? ChatColor.GREEN : ChatColor.RED;
            commandSender.sendMessage(ChatColor.GRAY + "- " + color + webhook.label + ChatColor.GRAY + ": " + ChatColor.WHITE + webhook.getDescription());
        }

        commandSender.sendMessage("");
        commandSender.sendMessage(
                ChatColor.GRAY + "--------------------["
                + ChatColor.LIGHT_PURPLE + " Page " + page + " of " + totalPages + ChatColor.GRAY
                + " ]--------------------"
        );
    }

    @Override
    public List<String> getSubcommandArguments(final CommandSender commandSender, final Command command, final String label, final String[] args) {
        // Calculate the number of pages
        int totalPages = (int) Math.ceil((double) WebHookEnum.values().length / ENTRIES_PER_PAGE);

        // Create a list of page numbers
        List<String> pageNumbers = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNumbers.add(String.valueOf(i));
        }

        return pageNumbers;
    }
}
