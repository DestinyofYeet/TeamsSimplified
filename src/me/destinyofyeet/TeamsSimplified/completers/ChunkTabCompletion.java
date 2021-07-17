package me.destinyofyeet.TeamsSimplified.completers;

import me.destinyofyeet.TeamsSimplified.main.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ChunkTabCompletion implements TabCompleter {
    final FileConfiguration config = Main.getPlugin().getConfig();

    private List<String> returnNothing(){
        return new ArrayList<String>(){{
            add("");
        }};
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("claim")){
            return returnNothing();

        } else if (command.getName().equals("unclaim")){
            return new ArrayList<String>(){{
                add("all");
            }};
        }
        return null;
    }
}
