package com.shweit.serverapi.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {
    void execute(CommandSender sender, String[] args);

    String getPermission();

    String getDescription();

    String getUsage();

    String getName();

    List<SubCommand> getSubCommands();  // Fügt Sub-Kommandos hinzu
}
