package me.destinyofyeet.TeamsSimplified.completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ToggleCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("toggle")){
            return new ArrayList<String>(){{
                add("enable");
                add("disable");
            }};
        }
        return null;
    }
}
