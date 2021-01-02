package me.pluginTest.commands;

import me.pluginTest.Main;
import net.minecraft.server.v1_16_R3.GameRules;
import org.bukkit.GameRule;
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
        for (int x = -5; x < 5; x++) {
            for (int z = -5; z < 5; z++) {
                ZombieTypes spawner = new ZombieTypes(plugin);
                spawner.createSpecialZombie(w, loc.add(x, 0, z));
            }
        }
        return false;
    }
}
