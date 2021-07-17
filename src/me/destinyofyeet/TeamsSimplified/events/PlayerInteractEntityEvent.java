package me.destinyofyeet.TeamsSimplified.events;

import me.destinyofyeet.TeamsSimplified.main.Main;
import me.destinyofyeet.TeamsSimplified.utils.TeamStuff;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerInteractEntityEvent implements Listener {
    final FileConfiguration config = Main.getPlugin().getConfig();
    private static final Set<Material> usableBlocks = new HashSet<Material>(Arrays.asList(
            Material.ENDER_CHEST,
            Material.CRAFTING_TABLE
    ));

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (event.getClickedBlock() == null){
            return;
        }

        Chunk blockLocation = event.getClickedBlock().getChunk();
        if (TeamStuff.isClaimed(blockLocation.getX(), blockLocation.getZ(), player.getWorld())){
            if (config.getBoolean("Chunks.AccessAnyway." + player.getUniqueId().toString()))
                return;
            if (usableBlocks.contains(event.getClickedBlock().getType()))
                return;
            if (!TeamStuff.isTeamClaimed(player, blockLocation.getX(), blockLocation.getZ())){
                player.sendMessage("§cThat chunk is claimed by another team, you can't interact with it!");
                event.setCancelled(true);
            }
        }
    }

}
