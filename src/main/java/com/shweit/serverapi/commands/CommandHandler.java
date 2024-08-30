package com.shweit.serverapi.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler implements CommandExecutor {

    private final Map<String, SubCommand> commands = new HashMap<>();

    public void register(String commandName, SubCommand subCommand) {
        commands.put(commandName.toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /mcapi <command>");
            return false;
        }

        String subCommandName = args[0].toLowerCase();
        SubCommand subCommand = commands.get(subCommandName);

        if (subCommand == null) {
            sender.sendMessage("Unknown command. Available commands: " + String.join(", ", commands.keySet()));
            return false;
        }

        subCommand.execute(sender, removeFirstArg(args));
        return true;
    }

    private String[] removeFirstArg(String[] args) {
        if (args.length <= 1) {
            return new String[0];
        }
        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, newArgs.length);
        return newArgs;
    }
}
