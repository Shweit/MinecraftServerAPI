package com.shweit.serverapi.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements TabExecutor {

    private ArrayList<SubCommand> subcommands = new ArrayList<>();

    public CommandManager(List<SubCommand> subcommands){
        this.subcommands.addAll(subcommands);
    }

    public ArrayList<SubCommand> getSubCommands(){
        return subcommands;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length > 0) {
            for (int i = 0; i < getSubCommands().size(); i++){
                if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName())){
                    getSubCommands().get(i).perform(commandSender, command, label, args);
                }
            }

        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> subcommands = new ArrayList<>();
            for (SubCommand subCommand : getSubCommands()) {
                subcommands.add(subCommand.getName());
            }
            return subcommands;
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
