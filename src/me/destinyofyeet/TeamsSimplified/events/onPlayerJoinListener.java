package me.destinyofyeet.TeamsSimplified.events;

import me.destinyofyeet.TeamsSimplified.main.Main;
import me.destinyofyeet.TeamsSimplified.utils.TeamStuff;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class onPlayerJoinListener implements Listener {
    final FileConfiguration config = Main.getPlugin().getConfig();

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        Player player = event.getPlayer();

        if (config.getStringList("Teams.PlayerInTeam." + player.getUniqueId().toString()).size() < 1){
            return;
        }

        TeamStuff.setName(player);
    }
}
