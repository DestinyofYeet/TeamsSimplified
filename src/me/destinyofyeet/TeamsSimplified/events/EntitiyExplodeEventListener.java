package me.destinyofyeet.TeamsSimplified.events;

import me.destinyofyeet.TeamsSimplified.utils.TeamStuff;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

import javax.swing.text.html.parser.Entity;

public class EntitiyExplodeEventListener implements Listener {

    @EventHandler
    public void onEntityExplodeEvent(EntityExplodeEvent event){
        Chunk chunk = event.getLocation().getChunk();

        if (TeamStuff.isClaimed(chunk.getX(), chunk.getZ(), event.getLocation().getWorld()))
            event.setCancelled(true);

    }
}
