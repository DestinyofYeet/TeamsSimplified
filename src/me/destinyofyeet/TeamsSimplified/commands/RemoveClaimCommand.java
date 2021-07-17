package me.destinyofyeet.TeamsSimplified.commands;

import me.destinyofyeet.TeamsSimplified.utils.TeamStuff;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveClaimCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("§cSorry, but only players can execute this command!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("TeamsSimplified.chunk.removeclaim")){
            player.sendMessage("§cInsufficient permissions!");
        }

        int checkX = player.getLocation().getChunk().getX();
        int checkZ = player.getLocation().getChunk().getZ();

        if (!TeamStuff.isClaimed(checkX, checkZ, player.getWorld())){
            player.sendMessage("§cThat chunk isn't claimed by anyone!");
            return true;
        }

        TeamStuff.removeChunkClaim(checkX, checkZ, player.getWorld());

        player.sendMessage("§aThat chunk got unclaimed!");

        return true;
    }
}
