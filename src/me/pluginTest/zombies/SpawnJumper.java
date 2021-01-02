package me.pluginTest.zombies;

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

public class SpawnJumper implements CommandExecutor{
    private Main plugin;

    public SpawnJumper(Main plugin){
        this.plugin=plugin;
        plugin.getCommand("summonjumper").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        Location loc = p.getLocation();
        World w = p.getWorld();
        p.getServer().broadcastMessage("Jumper spawned");
        Entity specialZombie = w.spawnEntity(loc, EntityType.ZOMBIE);
        Zombie zombie = (Zombie) specialZombie;
        zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(50);
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,
                1000000, 4));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000, 0));
        zombie.setCustomName("Jumper");
        zombie.getServer().broadcastMessage("jumper Health: "+zombie.getHealth());
        zombie.setMetadata("DamageAddOn", new FixedMetadataValue(plugin,
                "Jumper"));
        BukkitTask isOnGround=
                zombie.getServer().getScheduler().runTaskTimer(plugin,
                        new Runnable(){
                            public void run(){
                                if (zombie.isDead()) {
                                    Bukkit.getScheduler().cancelTask(zombie.getMetadata("Jumper").get(0).asInt());
                                    // zombie.getServer().broadcastMessage("Zombie " + zombie.getEntityId() + "
                                    // dead. climbCycle cancelled");
                                }
                                if (!zombie.isOnGround())
                                    zombie.removePotionEffect(PotionEffectType.JUMP);
                                else if(!zombie.hasPotionEffect(PotionEffectType.JUMP))
                                    zombie.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 4));
                            }
                        }, 5, 5);
        zombie.setMetadata("Jumper", new FixedMetadataValue(plugin,
                isOnGround.getTaskId()));
        return true;
    }
}
