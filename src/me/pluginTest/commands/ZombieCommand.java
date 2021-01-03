package me.pluginTest.commands;

import java.text.Format;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import me.pluginTest.Main;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;

public class ZombieCommand implements CommandExecutor {
    private Main plugin;

    public ZombieCommand(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("summontank").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        Location loc = p.getLocation();
        World w = p.getWorld();
        Entity specialZombie = w.spawnEntity(loc, EntityType.ZOMBIE);
        Zombie zombie = (Zombie) specialZombie;
        zombie.getServer().broadcastMessage("Tank spawned");
        zombie.getEquipment().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
        zombie.getEquipment().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
        zombie.getEquipment().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
        zombie.getEquipment().setBoots(new ItemStack(Material.NETHERITE_BOOTS));
        zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(12);
        zombie.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(5);
        zombie.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(.95);
        zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(50);
        zombie.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(.25);
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 1000000, 4));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1000000, 9));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 2));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 2, 100));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1000000, 1));
        zombie.setAdult();
        zombie.getEquipment().setHelmetDropChance(0.15f);
        zombie.getEquipment().setChestplateDropChance(0.05f);
        zombie.getEquipment().setLeggingsDropChance(0.1f);
        zombie.getEquipment().setBootsDropChance(0.2f);
        zombie.setCustomName("Tank");
        zombie.setMetadata("Tank", new FixedMetadataValue(plugin, "test"));
        BukkitTask checkCollision = zombie.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            public void run() {
                if (zombie.isDead()) {
                    Bukkit.getScheduler().cancelTask(zombie.getMetadata("climbCycle").get(0).asInt());
                    // zombie.getServer().broadcastMessage("Zombie " + zombie.getEntityId() + "
                    // dead. climbCycle cancelled");
                }
                if (!(zombie.getTarget() == null)) {
                    LivingEntity target = zombie.getTarget();
                    Vector attackVector = new Vector(target.getLocation().getX() - zombie.getLocation().getX(),
                            target.getLocation().getY() - zombie.getLocation().getY(),
                            target.getLocation().getZ() - zombie.getLocation().getZ());
                    boolean canClimb = false;
                    Vector climbVector = new Vector(0, 1, 0);
                    if (attackVector.getY() > 0) {
                        for (int x = -1; x <= 1; x++) {
                            for (int z = -1; z <= 1; z++) {
                                if (x == 0 && z == 0)
                                    continue;
                                if (zombie.getLocation().add(new Vector(x, 1, z)).getBlock().getType().isSolid()) {
                                    canClimb = true;
                                    break;
                                } else if (zombie.getLocation().add(new Vector(x, 0, z)).getBlock().getType()
                                        .isSolid()) {
                                    climbVector = new Vector(x, 1, z);
                                    break;
                                }
                            }
                        }
                        if (canClimb) {
                            zombie.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 1000000, 4));
                        } else if (zombie.hasPotionEffect(PotionEffectType.LEVITATION)) {
                            zombie.setCollidable(false);
                            zombie.teleport(zombie.getLocation().add(climbVector.normalize()).add(0, 0.5, 0));
                            zombie.removePotionEffect(PotionEffectType.LEVITATION);
                        }
                    } else if (zombie.hasPotionEffect(PotionEffectType.LEVITATION)) {
                        zombie.setCollidable(true);
                        zombie.removePotionEffect(PotionEffectType.LEVITATION);
                    }

                } else {
                    zombie.removePotionEffect(PotionEffectType.LEVITATION);
                }
            }
        }, 5, 5);
        zombie.setMetadata("climbCycle", new FixedMetadataValue(plugin, checkCollision.getTaskId()));
        return true;
    }

}