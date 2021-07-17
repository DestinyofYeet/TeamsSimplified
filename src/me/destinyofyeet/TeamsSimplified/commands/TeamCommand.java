package me.destinyofyeet.TeamsSimplified.commands;

import me.destinyofyeet.TeamsSimplified.main.Main;
import me.destinyofyeet.TeamsSimplified.utils.*;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamCommand implements CommandExecutor {
    final FileConfiguration config = Main.getPlugin().getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("TeamsSimplified.team.team")){
            sender.sendMessage("§cInsufficient permissions!");
            return true;
        }

        if (args.length < 1){
            return false;
        }

        if (args[0].equalsIgnoreCase("create")){
            if (!(sender instanceof Player)){
                sender.sendMessage("§cSorry, but you have to be a player to create teams!");
                return true;
            }
            Player player = (Player) sender;

            if (args.length < 2){
                player.sendMessage("§cYou need to provide a name for your team!");
            } else {
                if (args.length < 3){
                    player.sendMessage("§cYou need to provide a tag for your team!");
                    return true;
                }
                String teamName = args[1];
                String teamTag = args[2];

                if (teamTag.length() > 5){
                    player.sendMessage("§cMaximum length of tags is 5!");
                    return true;
                }

                String allTeamsPath = "Teams.AllTeams";
                List<String> allTeams = config.getStringList(allTeamsPath);

                String allTagsPath = "Teams.AllTags";
                List<String> allTags = config.getStringList(allTagsPath);

                if (allTeams.contains(teamName)){
                    player.sendMessage("§cSorry, a team with that name already exists!");
                    return true;
                }

                if (allTags.contains(teamTag)){
                    player.sendMessage("§cSorry, a team with that tag already exists!");
                    return true;
                }

                String playerPath = "Teams.PlayerInTeam." + player.getUniqueId();
                List<String> inTeam = config.getStringList(playerPath);

                if (inTeam.size() == 1){
                    player.sendMessage("§cSorry, you can be in one team at a time!");
                    return true;
                }

                String teamPath = "Teams.PlayerTeams." + teamName;
                config.set(teamPath + ".tag", teamTag);
                config.set(teamPath + ".color", "");
                config.set(teamPath + ".Owner", player.getUniqueId().toString());
                List<String> playersInTeam = new ArrayList<String>(){{
                    add(player.getUniqueId().toString());
                }};
                config.set(teamPath + ".PlayersInTeam", playersInTeam);
                config.set(teamPath + ".Moderators", new ArrayList<String>());
                config.set(teamPath + ".invites", new ArrayList<String>());
                config.set(teamPath + ".isOpen", false);
                config.set(teamPath + ".allChunks", new ArrayList<ArrayList<String>>());

                inTeam.add(teamName);
                config.set(playerPath, inTeam);

                allTeams.add(teamName);
                config.set(allTeamsPath, allTeams);

                allTags.add(teamTag);
                config.set(allTagsPath, allTags);

                TeamStuff.setName(player);

                Main.getPlugin().saveConfig();
                player.sendMessage("§aSuccessfully created team §6" + teamName + "§a!");
            }
            return true;

        } else if (args[0].equalsIgnoreCase("delete")){
            if (!(args.length == 2)){
                sender.sendMessage("§cYou need to specify a team to delete!");
                return true;
            }
            List<String> allTeams = config.getStringList("Teams.AllTeams");
            if (sender.hasPermission("TeamsSimplified.team.delete.others")){
                if (allTeams.contains(args[1])){
                    sender.sendMessage("§cPlease run §6/team confirmdelete " + args[1] + "§c to confirm deletion!");
                    return true;
                }

            } else {
                if (!(sender instanceof Player)){
                    sender.sendMessage("§cSorry, but if you don't have permissions to delete all teams, you need to be a player to perform this command!");
                    return true;
                }
                Player player = (Player) sender;
                if (allTeams.contains(args[1])&& Bukkit.getPlayer(UUID.fromString(config.getString("Teams.PlayerTeams." + args[1] + ".Owner"))).equals(player)){
                    player.sendMessage("§cPlease run §6/team confirmdelete " + args[1] + "§c to confirm deletion!");
                    return true;
                }
            }

        } else if (args[0].equalsIgnoreCase("confirmdelete")) {
            if (!(args.length == 2)) {
                sender.sendMessage("§cYou need to specify a team to delete!");
                return true;
            }
            List<String> allTeams = config.getStringList("Teams.AllTeams");
            List<String> allTags = config.getStringList("Teams.AllTags");
            if (sender.hasPermission("TeamsSimplified.team.delete.others")) {
                if (allTeams.contains(args[1])) {
                    TeamStuff.deleteTeam(args[1]);
                    Main.getPlugin().saveConfig();
                    return true;
                }

            } else {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cSorry, but if you don't have permissions to delete all teams, you need to be a player to perform this command!");
                    return true;
                }
                Player player = (Player) sender;
                if (allTeams.contains(args[1]) && Bukkit.getPlayer(UUID.fromString(config.getString("Teams.PlayerTeams." + args[1] + ".Owner"))).equals(player)) {
                    TeamStuff.deleteTeam(args[1]);
                    Main.getPlugin().saveConfig();
                    return true;
                }
            }

        } else if (args[0].equalsIgnoreCase("promote")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cSorry, you have to be player to invite somebody into your team!");
                return true;
            }

            Player player = (Player) sender;

            if (!TeamStuff.isInTeam(player)) {
                player.sendMessage("§cSorry, you have to be in a team to promote somebody!");
                return true;
            }

            if (!TeamStuff.isOwner(player)) {
                player.sendMessage("§cSorry, you have to be the team owner to promote somebody!");
                return true;
            }

            if (args.length < 2) {
                player.sendMessage("§cYou need to provide a player to promote!");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (!Bukkit.getOnlinePlayers().contains(target)) {
                player.sendMessage("§cSorry, but that player isn't online!");
                return true;
            }

            if (!TeamStuff.returnTeamName(player).equals(TeamStuff.returnTeamName(target))) {
                player.sendMessage("§cYou have to be in the same team!");
                return true;
            }

            String teamName = TeamStuff.returnTeamName(player);

            List<String> moderatorList = config.getStringList("Teams.PlayerTeams." + teamName + ".Moderators");
            if (!moderatorList.contains(target.getUniqueId().toString()))
                moderatorList.add(target.getUniqueId().toString());
            else {
                player.sendMessage("§cThat person is already a moderator!");
                return true;
            }
            config.set("Teams.PlayerTeams." + teamName + ".Moderators", moderatorList);

            player.sendMessage("§aSuccessfully promoted §6" + target.getName() + "§a to moderator!");

            Main.getPlugin().saveConfig();
            return true;

        } else if (args[0].equalsIgnoreCase("demote")){
            if (!(sender instanceof Player)){
                sender.sendMessage("§cSorry, you have to be a player to demote somebody!");
                return true;
            }

            Player player = (Player) sender;

            if (!TeamStuff.isOwner(player)){
                player.sendMessage("§cSorry, you have to be the owner to demote somebody!");
                return true;
            }

            if (args.length < 2){
                player.sendMessage("§cYou need to provide a player to demote!");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (!Bukkit.getOnlinePlayers().contains(target)) {
                player.sendMessage("§cSorry, but that player isn't online!");
                return true;
            }

            if (!TeamStuff.returnTeamName(player).equals(TeamStuff.returnTeamName(target))) {
                player.sendMessage("§cYou have to be in the same team!");
                return true;
            }

            String teamName = TeamStuff.returnTeamName(player);

            List<String> moderatorList = config.getStringList("Teams.PlayerTeams." + teamName + ".Moderators");

            if (!moderatorList.contains(target.getUniqueId().toString())){
                player.sendMessage("§cThat person isn't a moderator!");
                return true;
            } else {
                moderatorList.remove(target.getUniqueId().toString());
            }

            config.set("Teams.PlayerTeams." + teamName + ".Moderators", moderatorList);
            player.sendMessage("§aSuccessfully demoted §6" + target.getName() + "§a!");
            Main.getPlugin().saveConfig();
            return true;

        } else if (args[0].equalsIgnoreCase("invite")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cSorry, you have to be a player to invite somebody into your team!");
                return true;
            }

            if (!(args.length > 1)) {
                sender.sendMessage("§cYou need to provide a player to invite!");
                return true;
            }

            Player player = (Player) sender;
            Player target = Bukkit.getPlayer(args[1]);

            if (!Bukkit.getOnlinePlayers().contains(target)) {
                player.sendMessage("§cThe player §6" + args[1] + "§c isn't online!");
                return true;
            }

            if (config.getStringList("Teams.PlayerInTeam." + player.getUniqueId().toString()).size() < 1) {
                player.sendMessage("§cYou are not in a team!");
                return true;
            }

            String teamName = config.getStringList("Teams.PlayerInTeam." + player.getUniqueId().toString()).get(0);

            if (config.getBoolean("Teams.PlayerTeams." + teamName + ".isOpen")) {
                player.sendMessage("§cYour team is open anyways, no need to invite!");
                return true;
            }

            if (!TeamStuff.isOwner(player) && !TeamStuff.isModerator(player)) {
                player.sendMessage("§cSorry, only moderators and owners can invite sombody to your team!");
                return true;
            }

            List<String> invites = config.getStringList("Teams.PlayerTeams." + teamName + ".invites");
            invites.add(target.getUniqueId().toString());
            config.set("Teams.PlayerTeams." + teamName + ".invites", invites);

            player.sendMessage("§aYou successfully invited §6" + target.getName() + "§a to join your team!");
            target.sendMessage("§aYou have been invited by §6" + player.getName() + "§a to join the team §6" + teamName + "§a! Do §6/team join " + teamName + "§a to accept the invite!");
            Main.getPlugin().saveConfig();
            return true;

        } else if (args[0].equalsIgnoreCase("delinvite")){
            if (!(sender instanceof Player)){
                sender.sendMessage("§cYou have to be a player to delete an invite!");
                return true;
            }

            Player player = (Player) sender;

            if (!TeamStuff.isInTeam(player)){
                player.sendMessage("§cYou need to be in a team to invite somebody!");
                return true;
            }

            if (!TeamStuff.isOwner(player) && !TeamStuff.isModerator(player)){
                player.sendMessage("§cYou are not the owner or a moderator of the team, so you can't delete an invite!");
                return true;
            }
            
            if (!(args.length > 1)){
                player.sendMessage("§cYou need to provide a player to delete the invite from!");
                return true;
            }
            
            Player target = Bukkit.getPlayer(args[1]);
            if (!Bukkit.getOnlinePlayers().contains(target)){
                player.sendMessage("§cSorry, that player isn't online!");
                return true;
            }

            String teamName = TeamStuff.returnTeamName(player);

            List<String> invites = config.getStringList("Teams.PlayerTeams." + teamName + ".invites");
            invites.remove(target.getUniqueId().toString());
            config.set("Teams.PlayerTeams." + teamName + ".invites", invites);
            
            player.sendMessage("§aSuccessfully revoked invite for §6" + target.getName() + "§a!");
            Main.getPlugin().saveConfig();
            return true;

        } else if (args[0].equalsIgnoreCase("join")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cYou have to be a player to join a team!");
                return true;
            }
            Player player = (Player) sender;

            if (!player.hasPermission("TeamsSimplified.team.join.join")) {
                player.sendMessage("§cInsufficient permissions!");
                return true;
            }

            String teamName = args[1];

            if (!config.getStringList("Teams.AllTeams").contains(teamName)) {
                player.sendMessage("§cSorry, that team doesn't exist!");
                return true;
            }

            if (!config.getBoolean("Teams.PlayerTeams." + teamName + ".isOpen") && !player.hasPermission("TeamsSimplified.team.join.joinanyways")) {
                if (!config.getStringList("Teams.PlayerTeams." + teamName + ".invites").contains(player.getUniqueId().toString())) {
                    player.sendMessage("§cSorry, that team isn't open and you aren't invited from that team!");
                    return true;
                }
            }
            if (TeamStuff.joinTeam(teamName, player))
                player.sendMessage("§aSuccessfully joined team §6" + teamName + "§a!");
            else
                player.sendMessage("§cSomething went wrong while trying to join the team! Perhaps you are already in a team or that team doesn't exist!");
            return true;

        } else if (args[0].equalsIgnoreCase("kick")){
            if (!(sender instanceof Player)){
                sender.sendMessage("§cYou need to be a player to use this command");
                return true;
            }
            Player player = (Player) sender;

            if (!TeamStuff.isInTeam(player)){
                player.sendMessage("§cYou need to be in a team to use this command");
                return true;
            }

            if (!TeamStuff.isOwner(player) && !TeamStuff.isModerator(player)){
                player.sendMessage("§cYou need to be the owner or a moderator of the team to use this command!");
                return true;
            }

            if (args.length < 2){
                player.sendMessage("§cYou need to provide a player to kick!");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            String uniqueId = null;
            if (target == null){
                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()){
                    if (offlinePlayer.getName().equals(args[1])){
                        uniqueId = offlinePlayer.getUniqueId().toString();
                    }
                }
            } else {
                uniqueId = target.getUniqueId().toString();
            }
            if (uniqueId == null){
                player.sendMessage("§cThat player has never been on the server!");
                return true;
            }
            TeamStuff.leaveTeam(uniqueId);
            player.sendMessage("§aSuccessfully kicked §6" + args[1] + "§a from your team!");
            return true;

        } else if (args[0].equalsIgnoreCase("leave")){
            if (!(sender instanceof Player)){
                sender.sendMessage("§cYou have to be a player to leave a team!");
                return true;
            }
            Player player = (Player) sender;

            if (!player.hasPermission("TeamsSimplified.team.leave")){
                player.sendMessage("§cInsufficient permissions!");
                return true;
            }


            if (TeamStuff.leaveTeam(player)){
                player.sendMessage("§aSuccessfully left team!");

            } else {
                player.sendMessage("§cSomething went wrong while trying to leave the team! Perhaps you aren't in any team or that team doesn't exist!");
            }

            return true;

        } else if (args[0].equalsIgnoreCase("info")){
            String teamName = null;
            if (args.length < 2){
                if (!(sender instanceof Player)){
                    sender.sendMessage("§cSorry, you have to be a player to get info about a team that you are in!");
                    return true;
                }
                Player player = (Player) sender;

                if (config.getStringList("Teams.PlayerInTeam." + player.getUniqueId()).size() == 0){
                    player.sendMessage("§cYou are in no team!");
                    return true;
                }

                teamName = config.getStringList("Teams.PlayerInTeam." + player.getUniqueId()).get(0);

            } else {
                teamName = args[1];
            }

            String pathToTeam = "Teams.PlayerTeams." + teamName;

            String tag = config.getString(pathToTeam + ".tag");
            String color = config.getString(pathToTeam + ".color");
            String owner = config.getString(pathToTeam + ".Owner");
            int chunkAmount = config.getIntegerList("Chunks.TeamChunks." + teamName + ".allChunks").size();
            List<String> players = config.getStringList(pathToTeam + ".PlayersInTeam");
            players.remove(owner);
            List<String> moderators = config.getStringList(pathToTeam + ".Moderators");

            if (color == null){
                sender.sendMessage("§cThat team doesn't exist!");
                return true;
            }

            for (String mod: moderators){
                players.remove(mod);
            }

            List<String> realPlayers = new ArrayList<String>(){{
                for (String playerName : players){
                    Player player = Bukkit.getPlayer(UUID.fromString(playerName));
                    if (player == null){
                        OfflinePlayer player2 = Bukkit.getOfflinePlayer(UUID.fromString(playerName));
                        add(player2.getName());
                    } else {
                        add(player.getName());
                    }
                }
            }};

            List<String> realMods = new ArrayList<String>(){{
                for (String modUUID: moderators){
                    Player player = Bukkit.getPlayer(UUID.fromString(modUUID));
                    if (player == null){
                        OfflinePlayer player2 = Bukkit.getOfflinePlayer(UUID.fromString(modUUID));
                        add(player2.getName());
                    } else {
                        add(player.getName());
                    }
                }
            }};

            sender.sendMessage("§6Team name: §" + color.replace("&", "§") + " " + teamName + "§r\n" +
                    "§6Team tag: " + color.replace("&", "§") + tag + "§r\n" +
                    "§6Team owner: §a" + Bukkit.getPlayer(UUID.fromString(owner)).getName() + "§r\n" +
                    "§6Team moderators: §a" + String.join(", ", realMods) + "§r\n" +
                    "§6Team players: §a" + String.join(", ", realPlayers) + "§r\n" +
                    "§6Chunks claimed: §a" + chunkAmount);

            return true;

        } else if (args[0].equalsIgnoreCase("list")){
            List<String> showedTeams = new ArrayList<String>() {{
                for (String teamName : config.getStringList("Teams.AllTeams")){
                    if (config.getBoolean("Teams.PlayerTeams." + teamName + ".isOpen"))
                        add(teamName);
                }
            }};

            if (!showedTeams.isEmpty())
                sender.sendMessage("§6Open teams to join: §a" + String.join(", ", showedTeams));

            else
                sender.sendMessage("§cThere are no open teams to join!");

            return true;

        } else if (!sender.hasPermission("TeamsSimplified.team.modify.modify")) {
            sender.sendMessage("§cInsufficient permissions!");
            return true;
        }

        if (!(args.length > 1)) {
            sender.sendMessage("§cYou need to provide a team name to edit!");
            return true;
        }

        String teamName = args[1];

        if (!config.getStringList("Teams.AllTeams").contains(teamName)) {
            sender.sendMessage("§cSorry, that team doesn't exist!");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cTo edit teams, you have to be a player!");
            return true;
        }

        Player player = (Player) sender;

        if (!sender.hasPermission("TeamsSimplified.team.modify.others")) {
            if (!config.getStringList("Teams.PlayerInTeam." + player.getUniqueId()).contains(teamName)) {
                player.sendMessage("§cSorry, you are not the owner of that team so you can't edit it!");
                return true;
            }

        }

        if (args.length > 2) {
            if (args[2].equalsIgnoreCase("name")) {
                if (!(args.length > 3)) {
                    player.sendMessage("§cYou have to provide a new team name!");
                    return true;
                }
                String newTeamName = args[3];

                if (config.getStringList("Teams.AllTeams").contains(newTeamName)) {
                    player.sendMessage("§cSorry, that team name already exists!");
                    return true;
                }

                String teamPath = "Teams.PlayerTeams." + teamName;

                String teamTag = config.getString(teamPath + ".tag");
                String teamColor = config.getString(teamPath + ".color");
                String teamOwner = config.getString(teamPath + ".Owner");

                List<String> teamPlayers = config.getStringList(teamPath + ".PlayersInTeam");
                List<String> teamModerators = config.getStringList(teamPath + ".Moderators");
                List<String> teamInvites = config.getStringList(teamPath + ".invites");
                boolean isOpen = config.getBoolean(teamPath + ".isOpen");

                String chunkPath = "Chunks.TeamChunks." + teamName;
                String newChunkpath = "Chunks.TeamChunks." + newTeamName;

                for (int currentChunk : config.getIntegerList(chunkPath + ".allChunks")) {
                    int oldX = config.getInt(chunkPath + "." + currentChunk + ".X");
                    int oldZ = config.getInt(chunkPath + "." + currentChunk + ".Z");
                    config.set(newChunkpath + "." + currentChunk + ".X", oldX);
                    config.set(newChunkpath + "." + currentChunk + ".Z", oldZ);
                }

                String allChunksTeamPath = "Chunks.AllTeams";

                List<String> allChunksTeam = config.getStringList(allChunksTeamPath);
                allChunksTeam.remove(teamName);
                allChunksTeam.add(newTeamName);

                config.set(allChunksTeamPath, allChunksTeam);
                config.set(newChunkpath + ".allChunks", config.getIntegerList(chunkPath + ".allChunks"));

                String newTeamPath = "Teams.PlayerTeams." + newTeamName;

                config.set(newTeamPath + ".tag", teamTag);
                config.set(newTeamPath + ".Owner", teamOwner);
                config.set(newTeamPath + ".PlayersInTeam", teamPlayers);
                config.set(newTeamPath + ".Moderators", teamModerators);
                config.set(newTeamPath + ".color", teamColor);
                config.set(newTeamPath + ".invites", teamInvites);
                config.set(newTeamPath + ".isOpen", isOpen);

                for (String playerUUID : teamPlayers) {
                    config.set("Teams.PlayerInTeam." + playerUUID, new ArrayList<String>() {{
                        add(newTeamName);
                    }});
                }
                List<String> allTeamsList = config.getStringList("Teams.AllTeams");
                allTeamsList.remove(teamName);
                allTeamsList.add(newTeamName);
                config.set("Teams.AllTeams", allTeamsList);
                Main.getPlugin().saveConfig();
                player.sendMessage("§aYour team got renamed to §6" + newTeamName + "§a!");
                return true;

            } else if (args[2].equalsIgnoreCase("tag")) {
                if (!(args.length > 3)) {
                    player.sendMessage("§cYou have to provide a new team tag!");
                    return true;
                }

                String newTag = args[3];

                if (config.getString("Teams.PlayerTeams." + teamName + ".tag").equals(newTag)) {
                    player.sendMessage("§cThats your own tag!");
                    return true;
                }

                if (config.getStringList("Teams.AllTags").contains(newTag)) {
                    player.sendMessage("§cSorry, that tag is already used by another team!");
                    return true;
                }

                List<String> allTags = config.getStringList("Teams.AllTags");
                allTags.remove(config.getString("Teams.PlayerTeams." + teamName + ".tag"));
                allTags.add(newTag);
                config.set("Teams.AllTags", allTags);

                config.set("Teams.PlayerTeams." + teamName + ".tag", newTag);
                player.sendMessage("§aYour team's tag got changed to §6" + newTag + "§a!");
                for (String playerString : config.getStringList("Teams.PlayerTeams." + teamName + ".PlayersInTeam")) {
                    Player playerToSetName = Bukkit.getPlayer(UUID.fromString(playerString));
                    if (Bukkit.getOnlinePlayers().contains(playerToSetName)) {
                        TeamStuff.setName(playerToSetName);
                    }
                }
                Main.getPlugin().saveConfig();
                return true;

            } else if (args[2].equalsIgnoreCase("color")) {
                if (!(args.length > 3)) {
                    player.sendMessage("§cYou have to provide a color!");
                    return true;
                }
                String newTeamColor = args[3];

                if (!newTeamColor.startsWith("&")) {
                    player.sendMessage("§cYour color has to start with a '&'. Set it to '&a' to get green for example. Lookup 'bukkit colorcodes' for more colors and formatting options!");
                    return true;
                }

                config.set("Teams.PlayerTeams." + teamName + ".color", newTeamColor.replace("&", "§"));
                player.sendMessage("§aYour color has been set to " + newTeamColor.replace("&", "§") + "this color" + "§a!");

                for (String playerUUID : config.getStringList("Teams.PlayerTeams." + teamName + ".PlayersInTeam")) {
                    Player playerToSetColor = Bukkit.getPlayer(UUID.fromString(playerUUID));
                    if (Bukkit.getOnlinePlayers().contains(playerToSetColor)) {
                        TeamStuff.setName(playerToSetColor);
                    }
                }

                Main.getPlugin().saveConfig();

                return true;

            } else if (args[2].equalsIgnoreCase("accessibility")) {
                if (!(args.length > 3)) {
                    player.sendMessage("§cYou have to provide an option!");
                    return true;
                }

                String newOption = args[3];

                if (newOption.equalsIgnoreCase("open")) {
                    config.set("Teams.PlayerTeams." + teamName + ".isOpen", true);
                    player.sendMessage("§aYour team is now §6open§a!");

                } else if (newOption.equalsIgnoreCase("closed")) {
                    config.set("Teams.PlayerTeams." + teamName + ".isOpen", false);
                    player.sendMessage("§aYour team is now §6closed§a!");

                } else {
                    player.sendMessage("§cInvalid option!");
                }
                Main.getPlugin().saveConfig();
                return true;
            }
        }

        sender.sendMessage("§cInvalid editing option!");
        return true;
    }
}
