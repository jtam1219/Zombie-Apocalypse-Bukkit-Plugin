package me.pluginTest.commands;

import me.pluginTest.Main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import me.pluginTest.zombies.ZombieTypes;

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
        p.getServer().broadcastMessage("Spawned 50 Zombies");
        ZombieTypes spawner = new ZombieTypes(plugin);
        for (int i = -5; i < 5; i++) {
            for (int j = -5; j < 5; j++) {
                spawner.createSpecialZombie(w, new Location(w, loc.getX() + i, loc.getY(), loc.getZ() + j));
            }
        }
        return false;
    }
}
