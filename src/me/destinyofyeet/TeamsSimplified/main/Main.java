package me.destinyofyeet.TeamsSimplified.main;

import me.destinyofyeet.TeamsSimplified.commands.*;
import me.destinyofyeet.TeamsSimplified.completers.ChunkTabCompletion;
import me.destinyofyeet.TeamsSimplified.completers.TeamTabCompletion;
import me.destinyofyeet.TeamsSimplified.completers.ToggleCompleter;
import me.destinyofyeet.TeamsSimplified.utils.*;
import me.destinyofyeet.TeamsSimplified.events.*;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getPluginManager;

public class Main extends JavaPlugin {
    public static Main TeamsSimplified;
    final FileConfiguration config = this.getConfig();

    @Override
    public void onEnable(){
        System.out.println("Started loading TeamsSimplified!");

        TeamsSimplified = this;

        getCommand("team").setExecutor(new TeamCommand());
        getCommand("team").setTabCompleter(new TeamTabCompletion());

        getCommand("claim").setExecutor(new ChunkClaimCommand());
        getCommand("claim").setTabCompleter(new ChunkTabCompletion());

        getCommand("unclaim").setExecutor(new ChunkUnclaimCommand());
        getCommand("unclaim").setTabCompleter(new ChunkTabCompletion());

        getCommand("removeclaim").setExecutor(new RemoveClaimCommand());

        getCommand("toggle").setExecutor(new ToggleCommand());
        getCommand("toggle").setTabCompleter(new ToggleCompleter());

        PluginManager manager = getPluginManager();
        manager.registerEvents(new onPlayerJoinListener(), this);
        manager.registerEvents(new PlayerHarvestBlockEventListener(), this);
        manager.registerEvents(new PlayerInteractEntityEvent(), this);
        manager.registerEvents(new EntitiyExplodeEventListener(), this);
        manager.registerEvents(new EntityDamageEventListener(), this);

        config.options().header("I will now quickly describe what each config does");
        config.options().header("MaxTeamNameLength: This is the maximum name length of a team name");
        config.options().header("ShouldShowTabList: Should the tablist be overwritten with the teamname and playername?");
        config.addDefault("Config.MaxTeamNameLength", 20);
        config.addDefault("Config.ShouldShowTabList", true);

        config.options().copyDefaults(true);
        config.options().copyHeader(true);

        new Metrics(this, 9742);

        this.saveConfig();

        System.out.println("Finished loading TeamsSimplified!");
    }

    public static Main getPlugin(){
        return TeamsSimplified;
    }
}
