package me.pluginTest.commands;

import me.pluginTest.Main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import me.pluginTest.zombies.ZombieTypes;
import org.bukkit.entity.Zombie;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class SpawnWailer implements CommandExecutor {
    private Main plugin;

    public SpawnWailer(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("summonwailer").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        Location loc = p.getLocation();
        World w = p.getWorld();
        p.getServer().broadcastMessage("Wailer spawned");
        Entity specialZombie = w.spawnEntity(loc, EntityType.ZOMBIE);
        Zombie zombie = (Zombie) specialZombie;
        zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(50);
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 0));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1000000, 2));
        zombie.setCustomName("Wailer");
        zombie.setMetadata("Wailer", new FixedMetadataValue(plugin, "Wailer"));
        return false;
    }
}
