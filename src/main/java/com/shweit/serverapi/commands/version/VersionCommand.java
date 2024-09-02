package com.shweit.serverapi.commands.version;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class VersionCommand extends SubCommand {
    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String getDescription() {
        return "Shows the current version of the plugin.";
    }

    @Override
    public String getSyntax() {
        return "/mcapi version";
    }

    @Override
    public void perform(CommandSender commandSender, Command command, String label, String[] args) {
        commandSender.sendMessage(ChatColor.GREEN + "MinecraftServerAPI version: " + ChatColor.GOLD + MinecraftServerAPI.getInstance().getDescription().getVersion());
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender commandSender, Command command, String label, String[] args) {
        return List.of();
    }
}
