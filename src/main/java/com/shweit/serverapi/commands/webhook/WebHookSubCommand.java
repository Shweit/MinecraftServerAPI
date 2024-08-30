package com.shweit.serverapi.commands.webhook;

import com.shweit.serverapi.commands.AbstractSubCommand;
import org.bukkit.command.CommandSender;

public class WebHookSubCommand extends AbstractSubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        executeSubCommand(sender, args);
    }

    @Override
    public String getPermission() {
        return "mcapi.webhooks";
    }

    @Override
    public String getDescription() {
        return "Manage the webhooks of the MinecraftServerAPI plugin.";
    }

    @Override
    public String getUsage() {
        return "/mcapi webhooks <command>";
    }

    @Override
    public String getName() {
        return "webhooks";
    }
}
