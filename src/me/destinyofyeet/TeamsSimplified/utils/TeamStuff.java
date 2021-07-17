package me.destinyofyeet.TeamsSimplified.utils;

import me.destinyofyeet.TeamsSimplified.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TeamStuff {
    private static final FileConfiguration config = Main.getPlugin().getConfig();

    public static Boolean isClaimed(int checkX, int checkZ, World world){
        for (String teamName: config.getStringList("Chunks.AllTeams")) {
            for (int currentChunk : config.getIntegerList("Chunks.TeamChunks." + teamName + ".allChunks")) {
                int chunkX = config.getInt("Chunks.TeamChunks." + teamName + "." + currentChunk + ".X");
                int chunkZ = config.getInt("Chunks.TeamChunks." + teamName + "." + currentChunk + ".Z");
                String worldName = config.getString("Chunks.TeamChunks." + teamName + "." + currentChunk + ".World");

                if (checkX == chunkX && checkZ == chunkZ) {
                    if (worldName.equals(world.getName())){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Integer whatNumberAreTheChunkCoords(String teamName, int checkX, int checkZ, World world){
        for (int currentChunk : config.getIntegerList("Chunks.TeamChunks." + teamName + ".allChunks")) {
            int chunkX = config.getInt("Chunks.TeamChunks." + teamName + "." + currentChunk + ".X");
            int chunkZ = config.getInt("Chunks.TeamChunks." + teamName + "." + currentChunk + ".Z");
            String worldName = config.getString("Chunks.TeamChunks." + teamName + "." + currentChunk + ".World");

            if (checkX == chunkX && checkZ == chunkZ) {
                if (worldName.equals(world.getName())){
                    return currentChunk;
                }

            }
        }
        return 0;
    }

    public static Boolean isTeamClaimed(Player player, int checkX, int checkZ){
        if (!isInTeam(player))
            return false;
        String teamName = returnTeamName(player);
        for (int currentChunk : config.getIntegerList("Chunks.TeamChunks." + teamName + ".allChunks")) {
            int chunkX = config.getInt("Chunks.TeamChunks." + teamName + "." + currentChunk + ".X");
            int chunkZ = config.getInt("Chunks.TeamChunks." + teamName + "." + currentChunk + ".Z");
            String worldName = config.getString("Chunks.TeamChunks." + teamName + "." + currentChunk + ".World");

            if (checkX == chunkX && checkZ == chunkZ) {
                if (worldName.equals(player.getLocation().getWorld().getName())){
                    return true;
                }
            }
        }
        return false;
    }

    public static String returnTeamName(Player player){
        return config.getStringList("Teams.PlayerInTeam." + player.getUniqueId()).get(0);
    }

    public static String returnTeamName(String uuid){
        return config.getStringList("Teams.PlayerInTeam." + uuid).get(0);
    }

    public static Boolean isOwner(Player player){
        return Objects.equals(config.getString("Teams.PlayerTeams." + returnTeamName(player) + ".Owner"), player.getUniqueId().toString());
    }

    public static Boolean isModerator(Player player){
        String teamName = returnTeamName(player);
        return config.getStringList("Teams.PlayerTeams." + teamName + ".Moderators").contains(player.getUniqueId().toString());
    }

    public static Boolean isInTeam(Player player){
        return isInTeam(player.getUniqueId().toString());
    }

    public static Boolean isInTeam(String uuid){
        try{
            config.getStringList("Teams.PlayerInTeam." + uuid).get(0);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static void setName(Player player){
        boolean shouldTab = config.getBoolean("Config.ShouldShowTabList");
        String teamName = config.getStringList("Teams.PlayerInTeam." + player.getUniqueId().toString()).get(0);
        String tag = config.getString("Teams.PlayerTeams." + teamName + ".tag");
        String color = config.getString("Teams.PlayerTeams." + teamName + ".color");

        if (color.length() > 1){
            player.setDisplayName("[" + color.replace("&", "§") + tag + "§r] " + player.getName());
            if (shouldTab)
                player.setPlayerListName("[" + color.replace("&", "§") + tag + "§r] " + player.getName());

        } else {
            player.setDisplayName("[" + tag + "§r] " + player.getName());
            if (shouldTab)
                player.setPlayerListName("[" + tag + "§r] " + player.getName());
        }

    }

    public static void clearName(Player player){
        player.setDisplayName(player.getName());
        player.setPlayerListName(player.getName());
    }

    public static boolean deleteTeam(String teamName){
        List<String> membersList = config.getStringList("Teams.PlayerTeams." + teamName + ".PlayersInTeam");
        for (String playerString : membersList) {
            List<String> inTeamList = config.getStringList("Teams.PlayerInTeam." + playerString);
            config.set("Teams.PlayerInTeam." + playerString, inTeamList.remove(teamName));
            Player player = Bukkit.getPlayer(UUID.fromString(playerString));
            if (Bukkit.getOnlinePlayers().contains(player)) {
                player.sendMessage("§aSince the team §6" + teamName + "§a got deleted, you are being kicked from it!");
                clearName(player);
            }
        }
        String tag = config.getString("Teams.PlayerTeams." + teamName + ".tag");
        List<String> allTags = config.getStringList("Teams.AllTags");
        List<String> allTeams = config.getStringList("Teams.AllTeams");
        allTeams.remove(teamName);
        allTags.remove(tag);
        config.set("Teams.AllTags", allTags);
        config.set("Teams.AllTeams", allTeams);

        List<String> allChunkTeams = config.getStringList("Chunks.AllTeams");
        allChunkTeams.remove(teamName);
        config.set("Chunks.AllTeams", allChunkTeams);

        config.set("Chunks.TeamChunks." + teamName + ".allChunks", new ArrayList<Integer>());

        Main.getPlugin().saveConfig();
        return true;
    }

    public static boolean joinTeam(String teamName, Player player){
        if (!config.getStringList("Teams.AllTeams").contains(teamName)){
            return false;
        }

        if (config.getStringList("Teams.PlayerInTeam." + player.getUniqueId().toString()).size() > 0){
            return false;
        }

        List<String> inviteList = config.getStringList("Teams.PlayerTeams." + teamName + ".invites");
        if (inviteList.contains(player.getUniqueId().toString())){
            inviteList.remove(player.getUniqueId().toString());
            config.set("Teams.PlayerTeams." + teamName + ".invites", inviteList);
        }

        List<String> membersList = config.getStringList("Teams.PlayerTeams." + teamName + ".PlayersInTeam");
        if (!membersList.contains(player.getUniqueId().toString()))
            membersList.add(player.getUniqueId().toString());
        config.set("Teams.PlayerTeams." + teamName + ".PlayersInTeam", membersList);

        config.set("Teams.PlayerInTeam." + player.getUniqueId().toString(), new ArrayList<String>(){{
            add(teamName);
        }});

        setName(player);

        Main.getPlugin().saveConfig();

        return true;
    }

    public static boolean leaveTeam(String uuid){
        List<String> teamsPlayerIsIn = config.getStringList("Teams.PlayerInTeam." + uuid);
        if (teamsPlayerIsIn.size() < 1){
            return false;
        }
        String teamName = teamsPlayerIsIn.get(0);

        if (!config.getStringList("Teams.AllTeams").contains(teamName)){
            return false;
        }

        String teamPath = "Teams.PlayerTeams" + teamName;

        List<String> memberList = config.getStringList("Teams.PlayerTeams." + teamName + ".PlayersInTeam");
        memberList.remove(uuid);
        List<String> moderatorList = config.getStringList("Teams.PlayerTeams." + teamName + ".Moderators");
        moderatorList.remove(uuid);

        config.set("Teams.PlayerTeams." + teamName + ".PlayersInTeam", memberList);
        config.set("Teams.PlayerTeams." + teamName + ".Moderator", moderatorList);

        config.set("Teams.PlayerInTeam." + uuid, new ArrayList<String>());

        if (memberList.size() < 1){
            deleteTeam(teamName);
            System.out.println("Team " + teamName + " got deleted since nobody was in it!");
        }

        if (uuid.equals(config.getString(teamPath + ".Owner"))){
            List<String> listToGetNewOwner = config.getStringList(teamPath + ".Moderators");
            if (listToGetNewOwner.size() == 0){
                listToGetNewOwner = config.getStringList(teamPath + ".PlayersInTeam");
            }

            String newOwner = listToGetNewOwner.get(0);
            config.set(teamPath + ".Owner", newOwner);
        }
        if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(UUID.fromString(uuid))))
            clearName(Bukkit.getPlayer(UUID.fromString(uuid)));
        Main.getPlugin().saveConfig();
        return true;
    }

    public static boolean leaveTeam(Player player){return leaveTeam(player.getUniqueId().toString());}


    public static List<String> returnNothing(){
        return new ArrayList<String>(){{
            add("");
        }};
    }

    public static void removeChunkClaim(int checkX, int checkZ, World world){
        // this function will copy all chunks so they are numbered from 1 upwards
        for (String teamName: config.getStringList("Chunks.AllTeams")) {
            for (int currentChunk : config.getIntegerList("Chunks.TeamChunks." + teamName + ".allChunks")) {
                int chunkX = config.getInt("Chunks.TeamChunks." + teamName + "." + currentChunk + ".X");
                int chunkZ = config.getInt("Chunks.TeamChunks." + teamName + "." + currentChunk + ".Z");
                String worldName = config.getString("Chunks.TeamChunks." + teamName + "." + currentChunk + ".World");

                if (checkX == chunkX && checkZ == chunkZ && worldName.equals(world.getName())) {
                    List<Integer> chunkList = config.getIntegerList("Chunks.TeamChunks." + teamName + ".allChunks");
                    chunkList.remove((Integer) currentChunk);

                    List<Integer> newList = new ArrayList<Integer>();

                    for (int chunk : chunkList) {
                        int newNumber = newList.size() + 1;
                        String chunkPath = "Chunks.TeamChunks." + teamName + "." + chunk;

                        int oldX = config.getInt(chunkPath + ".X");
                        int oldZ = config.getInt(chunkPath + ".Z");
                        String oldWorldName = config.getString(chunkPath + ".World");

                        String newChunkPath = "Chunks.TeamChunks." + teamName + "." + newNumber;

                        config.set(newChunkPath + ".X", oldX);
                        config.set(newChunkPath + ".Z", oldZ);
                        config.set(newChunkPath + ".World", oldWorldName);

                        newList.add(newNumber);
                    }
                    config.set("Chunks.TeamChunks." + teamName + ".allChunks", newList);
                    Main.getPlugin().saveConfig();
                    break;
                }
            }
        }
    }
}
