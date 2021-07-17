package me.destinyofyeet.TeamsSimplified.commands;

import me.destinyofyeet.TeamsSimplified.main.Main;
import me.destinyofyeet.TeamsSimplified.utils.TeamStuff;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ChunkUnclaimCommand implements CommandExecutor {
    final FileConfiguration config = Main.getPlugin().getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("§cSorry, only players can use that command!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("TeamsSimplified.chunk.unclaim")){
            player.sendMessage("§cInsufficient permissions!");
            return true;
        }

        if (!TeamStuff.isInTeam(player)){
            player.sendMessage("§cYou aren't in a team!");
            return true;
        }

        if (!TeamStuff.isOwner(player) && !TeamStuff.isModerator(player)){
            player.sendMessage("§cYou aren't the owner or a moderator of your team!");
            return true;
        }

        if (args.length == 1 && args[0].equals("all")){
            config.set("Chunks.TeamChunks." + TeamStuff.returnTeamName(player) + ".allChunks", new ArrayList<Integer>());
            player.sendMessage("§aSuccessfully unclaimed all chunks!");
            Main.getPlugin().saveConfig();
            return true;
        }

        int chunkX = player.getLocation().getChunk().getX();
        int chunkZ = player.getLocation().getChunk().getZ();

        if (!TeamStuff.isTeamClaimed(player, chunkX, chunkZ)){
            player.sendMessage("§cThat chunk is either not claimed or not claimed by your team!");
            return true;
        }

        List<Integer> chunkList = config.getIntegerList("Chunks.TeamChunks." + TeamStuff.returnTeamName(player) + ".allChunks");

        int chunkToRemove = TeamStuff.whatNumberAreTheChunkCoords(TeamStuff.returnTeamName(player), chunkX, chunkZ, player.getWorld());

        if (chunkToRemove == 0 || !chunkList.contains(chunkToRemove)){
            player.sendMessage("§cSorry, something went wrong! If this keeps happening please contact the server admin!");
            return true;
        }

        TeamStuff.removeChunkClaim(chunkX, chunkZ, player.getWorld());

        player.sendMessage("§aSuccessfully unclaimed chunk!");

        Main.getPlugin().saveConfig();

        return true;
    }
}
