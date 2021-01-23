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
import org.bukkit.enchantments.Enchantment;
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

public class SpawnBulwark implements CommandExecutor {
    private Main plugin;

    public SpawnBulwark(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("summonbulwark").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        Location loc = p.getLocation();
        World w = p.getWorld();
        Entity specialZombie = w.spawnEntity(loc, EntityType.ZOMBIE);
        Zombie zombie = (Zombie) specialZombie;
        zombie.getServer().broadcastMessage("Bulwark spawned");
        zombie.setAdult();
        zombie.setMetadata("bulwark", new FixedMetadataValue(plugin, "bulwark"));
        ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        chestplate.addEnchantment(Enchantment.PROTECTION_FIRE, 4);
        chestplate.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 4);
        chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        zombie.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
        zombie.getEquipment().setChestplate(chestplate);
        zombie.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        zombie.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
        zombie.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_AXE));
        zombie.getEquipment().setItemInOffHand(new ItemStack(Material.SHIELD));
        zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(50);
        zombie.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(2);
        zombie.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(.5);
        zombie.getEquipment().setHelmetDropChance(0.05f);
        zombie.getEquipment().setChestplateDropChance(0.01f);
        zombie.getEquipment().setLeggingsDropChance(0.05f);
        zombie.getEquipment().setBootsDropChance(0.05f);
        zombie.getEquipment().setItemInMainHandDropChance(0.05f);
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 1));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST,
                1000000, 19));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 1, 100));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 0));
        return true;
    }
}