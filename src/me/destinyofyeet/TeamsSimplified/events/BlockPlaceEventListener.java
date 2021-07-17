package me.destinyofyeet.TeamsSimplified.events;

import me.destinyofyeet.TeamsSimplified.main.Main;
import me.destinyofyeet.TeamsSimplified.utils.TeamStuff;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceEventListener implements Listener {
    final FileConfiguration config = Main.getPlugin().getConfig();

    @EventHandler
    public void onBlockPlaceEvent (BlockPlaceEvent event){
        Player player = event.getPlayer();
        Chunk chunk = event.getBlockPlaced().getChunk();

        if (TeamStuff.isClaimed(chunk.getX(), chunk.getZ(), player.getWorld())){
            if (config.getBoolean("Chunks.AccessAnyway." + player.getUniqueId().toString()))
                return;
            if (!TeamStuff.isTeamClaimed(player, chunk.getX(), chunk.getZ())){
                player.sendMessage("§cThat chunk is claimed by another team, you can't interact with it!");
                event.setCancelled(true);
            }
        }
    }
}
