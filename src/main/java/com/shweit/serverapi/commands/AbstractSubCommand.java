package com.shweit.serverapi.commands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSubCommand implements SubCommand {

    private final List<SubCommand> subCommands = new ArrayList<>();

    public void registerSubCommand(SubCommand subCommand) {
        subCommands.add(subCommand);
    }

    @Override
    public List<SubCommand> getSubCommands() {
        return subCommands;
    }

    protected void executeSubCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(getUsage());
            return;
        }

        String subCommandName = args[0];
        SubCommand subCommand = subCommands.stream()
                .filter(cmd -> cmd.getName().equalsIgnoreCase(subCommandName))
                .findFirst()
                .orElse(null);

        if (subCommand == null) {
            sender.sendMessage("Unknown subcommand. Available commands: " + String.join(", ", getSubCommandNames()));
            return;
        }

        if (!sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage("You don't have permission to execute this command.");
            return;
        }

        // Entferne das erste Argument und rufe das Sub-Kommando auf
        subCommand.execute(sender, removeFirstArg(args));
    }

    private String[] removeFirstArg(String[] args) {
        if (args.length <= 1) {
            return new String[0];
        }
        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, newArgs.length);
        return newArgs;
    }

    private List<String> getSubCommandNames() {
        List<String> names = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            names.add(subCommand.getName().toLowerCase());
        }
        return names;
    }
}
