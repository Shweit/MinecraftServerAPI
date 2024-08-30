package com.shweit.serverapi.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public final class CommandManager implements TabExecutor {

    private final ArrayList<SubCommand> subcommands = new ArrayList<>();

    public CommandManager(final List<SubCommand> subCommandList) {
        this.subcommands.addAll(subCommandList);
    }

    public ArrayList<SubCommand> getSubCommands() {
        return subcommands;
    }

    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label, final String[] args) {
        if (args.length > 0) {
            for (int i = 0; i < getSubCommands().size(); i++) {
                if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName())) {
                    getSubCommands().get(i).perform(commandSender, command, label, args);
                }
            }

        }

        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length == 1) {
            List<String> subcommandsList = new ArrayList<>();
            for (SubCommand subCommand : getSubCommands()) {
                subcommandsList.add(subCommand.getName());
            }
            return subcommandsList;
        } else if (args.length > 1) {
            for (SubCommand subCommand : getSubCommands()) {
                if (args[0].equalsIgnoreCase(subCommand.getName())) {
                    return subCommand.getSubcommandArguments(sender, command, alias, args);
                }
            }
        }

        return null;
    }
}
