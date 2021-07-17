package me.destinyofyeet.TeamsSimplified.events;

import me.destinyofyeet.TeamsSimplified.utils.TeamStuff;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.Arrays;

public class EntityDamageEventListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        Chunk chunk = event.getEntity().getLocation().getChunk();

        if (TeamStuff.isClaimed(chunk.getX(), chunk.getZ(), event.getEntity().getLocation().getWorld())){
            if (Arrays.asList(DamageCause.BLOCK_EXPLOSION, DamageCause.ENTITY_EXPLOSION).contains(event.getCause())){
                event.setCancelled(true);
            }
        }
    }
}
