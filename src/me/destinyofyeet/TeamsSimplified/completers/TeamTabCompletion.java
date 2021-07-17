package me.destinyofyeet.TeamsSimplified.completers;

import me.destinyofyeet.TeamsSimplified.main.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeamTabCompletion implements TabCompleter {
    final FileConfiguration config = Main.getPlugin().getConfig();

    private List<String> returnNothing(){
        return new ArrayList<String>(){{
            add("");
        }};
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("team")){
            if (args.length == 1){
                return new ArrayList<String>(){{
                    add("create");
                    add("delete");
                    add("edit");
                    add("join");
                    add("leave");
                    add("invite");
                    add("delinvite");
                    add("list");
                    add("confirmdelete");
                    add("info");
                    add("promote");
                    add("demote");
                    add("kick");
                }};

            } else if (args.length == 2 && (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("confirmdelete") || args[0].equalsIgnoreCase("edit"))){
                if (sender instanceof ConsoleCommandSender ){
                    return config.getStringList("Teams.AllTeams");
                } else if(sender instanceof Player){
                    Player player = (Player) sender;
                    if (player.hasPermission("TeamsSimplified.team.delete.others")){
                        return config.getStringList("Teams.AllTeams");
                    } else {
                        return config.getStringList("Teams.PlayerInTeam." + player.getUniqueId());
                    }
                }

            } else if (args.length == 2 && args[0].equalsIgnoreCase("edit")){
                if (sender.hasPermission("TeamsSimplified.team.modify.others") || !(sender instanceof Player)){
                    return config.getStringList("Teams.AllTeams");
                }
                Player player = (Player) sender;

                return config.getStringList("Teams.PlayerInTeam." + player.getUniqueId());


            } else if (args.length == 3 && args[0].equalsIgnoreCase("edit")) {
                return new ArrayList<String>() {{
                    add("name");
                    add("tag");
                    add("color");
                    add("accessibility");
                }};

            } else if (args.length == 4 && args[0].equalsIgnoreCase("edit") && args[2].equalsIgnoreCase("accessibility")) {
                return new ArrayList<String>() {{
                    add("open");
                    add("closed");
                }};

            } else if (args.length == 4  && args[0].equalsIgnoreCase("edit") && args[2].equalsIgnoreCase("color")) {
                return returnNothing();

            } else if (args.length == 4  && args[0].equalsIgnoreCase("edit") && args[2].equalsIgnoreCase("tag")) {
                return returnNothing();

            } else if (args.length == 4  && args[0].equalsIgnoreCase("edit") && args[2].equalsIgnoreCase("name")) {
                return returnNothing();

            } else if (args.length == 2 && args[0].equalsIgnoreCase("join")){
                if (!(sender instanceof Player)){
                    return returnNothing();
                }
                List<String> availableTeamsToJoin = new ArrayList<String>();
                Player player = (Player) sender;

                for (String teamName: config.getStringList("Teams.AllTeams")){
                    if (config.getBoolean("Teams.PlayerTeams." + teamName + ".isOpen") || config.getStringList("Teams.PlayerTeams." + teamName + ".invites").contains(player.getUniqueId().toString()) || player.hasPermission("TeamsSimplified.team.join.joinanyways")){
                        availableTeamsToJoin.add(teamName);
                    }
                }
                return availableTeamsToJoin;
            }
        }
        return null;
    }
}
