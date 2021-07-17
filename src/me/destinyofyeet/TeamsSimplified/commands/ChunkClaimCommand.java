package me.destinyofyeet.TeamsSimplified.commands;

import me.destinyofyeet.TeamsSimplified.main.Main;
import me.destinyofyeet.TeamsSimplified.utils.TeamStuff;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class ChunkClaimCommand implements CommandExecutor {
    final FileConfiguration config = Main.getPlugin().getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("§cSorry, you have to be a player to use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("TeamsSimplified.chunk.claim")){
            player.sendMessage("§cInsufficient permissions!");
            return true;
        }

        if (!TeamStuff.isInTeam(player)){
            player.sendMessage("§cYou aren't in a team!");
            return true;
        }

        if (!TeamStuff.isOwner(player) && !TeamStuff.isModerator(player)){
            player.sendMessage("§cYou are not a mod neither the owner of the team so you can't claim chunks!");
            return true;
        }

        if (TeamStuff.isClaimed(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ(), player.getWorld())){
            player.sendMessage("§cThat chunk is already claimed!");
            return true;
        }

        String teamName = TeamStuff.returnTeamName(player);

        List<Integer> allChunks = config.getIntegerList("Chunks.TeamChunks." + teamName + ".allChunks");

        int nextChunk;
        if (allChunks.size() == 0){
            nextChunk = 1;
        } else {
            nextChunk = allChunks.size() + 1;
        }

        allChunks.add(nextChunk);

        int chunkX = player.getLocation().getChunk().getX();
        int chunkZ = player.getLocation().getChunk().getZ();

        String chunkPath = "Chunks.TeamChunks." + teamName + "." + nextChunk;

        config.set(chunkPath + ".X", chunkX);
        config.set(chunkPath + ".Z", chunkZ);
        config.set(chunkPath + ".World", player.getLocation().getWorld().getName());

        config.set("Chunks.TeamChunks." + teamName + ".allChunks", allChunks);

        List<String> allTeamsClaimed = config.getStringList("Chunks.AllTeams");

        if (!allTeamsClaimed.contains(teamName))
            allTeamsClaimed.add(teamName);

        config.set("Chunks.AllTeams", allTeamsClaimed);

        Main.getPlugin().saveConfig();

        player.sendMessage("§aSuccessfully claimed chunk!");

        return true;
    }
}
