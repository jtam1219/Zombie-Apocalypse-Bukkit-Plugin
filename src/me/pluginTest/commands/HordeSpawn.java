package me.pluginTest.commands;

import me.pluginTest.Main;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class HordeSpawn implements CommandExecutor {
    private Main plugin;

    public HordeSpawn(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("summonhorde").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        Location loc = p.getLocation();
        World w = p.getWorld();
        for (int i=0; i<50; i++){
            p.getServer().broadcastMessage("Spawned 50 Zombies");
            w.spawnEntity(loc, EntityType.ZOMBIE);
        }
        return false;
    }
}
