package com.shweit.serverapi.commands.webhook;

import com.shweit.serverapi.commands.SubCommand;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ListWebHookCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        StringBuilder hookList = new StringBuilder("Registered WebHooks: \n");
        for (WebHookEnum hook : WebHookEnum.values()) {
            hookList.append(hook.getLabel()).append(" - ").append(hook.getDescription()).append("\n");
        }

        sender.sendMessage(hookList.toString());
    }

    @Override
    public String getPermission() {
        return "mcapi.webhooks.list";
    }

    @Override
    public String getDescription() {
        return "This command lists all available webhooks";
    }

    @Override
    public String getUsage() {
        return "/mcapi webhooks list";
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public List<SubCommand> getSubCommands() {
        return null;
    }
}
