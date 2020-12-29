package me.pluginTest.commands;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import me.pluginTest.Main;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;


public class ZombieCommand implements CommandExecutor{
    private Main plugin;

    public ZombieCommand(Main plugin){
        this.plugin=plugin;
        plugin.getCommand("summon tank").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args){
        Player p=(Player) sender;
        Location loc=p.getLocation();
        World w=p.getWorld();
        Entity specialZombie = w.spawnEntity(loc, EntityType.ZOMBIE);
        Zombie tank=(Zombie) specialZombie;
        tank.getEquipment().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
        tank.getEquipment().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
        tank.getEquipment().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
        tank.getEquipment().setBoots(new ItemStack(Material.NETHERITE_BOOTS));
        tank.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));
        tank.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 1000000, 9));
        tank.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1000000, 9));
        tank.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 3));
        tank.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 5, 100));
        tank.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1000000, 1));
        tank.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 0));
        tank.setCustomName("Tank");
        Server s=p.getServer();
        s.broadcastMessage("Tank Summoned.");
        return false;
    }

}