package me.destinyofyeet.TeamsSimplified.commands;

import me.destinyofyeet.TeamsSimplified.main.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ToggleCommand implements CommandExecutor {
    final FileConfiguration config = Main.getPlugin().getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("§cSorry, but only players can execute this command!");
            return true;
        }

        if (!(sender.hasPermission("TeamsSimplified.chunk.accessanyway"))){
            sender.sendMessage("§cInsufficient permissions!");
            return true;
        }

        Player player = (Player) sender;

        boolean isEnabled = config.getBoolean("Chunks.AccessAnyway." + player.getUniqueId().toString(), false);

        if (args.length == 0){
            if (isEnabled)
                player.sendMessage("§aThe toggle to access another teams stuff is §6enabled!§a");
            else
                player.sendMessage("§aThe toggle to access another teams stuff is §6disabled§a!");
            return true;
        }

        String option = args[0];

        if (option.equalsIgnoreCase("enable")) {
            if (isEnabled)
                player.sendMessage("§cToggle is already enabled!");
            else {
               config.set("Chunks.AccessAnyway." + player.getUniqueId().toString(), true);
               player.sendMessage("§aToggle has been §6enabled§a!");
            }
        } else if (option.equalsIgnoreCase("disable")){
            if (!isEnabled)
                player.sendMessage("§cToggle is already disabled!");
            else {
                config.set("Chunks.AccessAnyway." + player.getUniqueId().toString(), false);
                player.sendMessage("§aToggle has been §6disabled§a!");
            }
            
        } else player.sendMessage("§cInvalid toggle option!");

        Main.getPlugin().saveConfig();
        return true;
    }
}
