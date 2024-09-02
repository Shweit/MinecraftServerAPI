package com.shweit.serverapi.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class SubCommand {

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract void perform(CommandSender commandSender, Command command, String label, String[] args);

    public abstract List<String> getSubcommandArguments(CommandSender commandSender, Command command, String label, String[] args);
}
