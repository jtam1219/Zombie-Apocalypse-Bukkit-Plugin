package me.pluginTest.commands;

import me.pluginTest.Main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import me.pluginTest.zombies.ZombieTypes;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class SpawnGiantTest implements CommandExecutor, Listener {
  private Main plugin;

  public SpawnGiantTest(Main plugin) {
    this.plugin = plugin;
    plugin.getCommand("summongiant").setExecutor(this);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    Player p = (Player) sender;
    Location loc = p.getLocation();
    World w = p.getWorld();
    p.getServer().broadcastMessage("Test Giant spawned");
    Entity ent = w.spawnEntity(loc, EntityType.GIANT);
    Giant giant = (Giant) ent;
    giant.getEquipment().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
    return true;
  }

  @EventHandler
  public void onAttackGiant(EntityDamageByEntityEvent e) {
    if (e.getEntity() instanceof Giant && e.getDamager() instanceof LivingEntity) {
      ((Giant) e.getEntity()).setTarget((LivingEntity) e.getDamager());
      ((Giant) e.getEntity()).attack(e.getDamager());
    }
  }
}
