package me.pluginTest.zombies;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class ZombieTypes implements Listener {

  @EventHandler
  public void onMobSpawn(CreatureSpawnEvent e) {
    EntityType type = e.getEntityType();
    if (type.equals(EntityType.CREEPER) || type.equals(EntityType.SPIDER) || type.equals(EntityType.CAVE_SPIDER)
        || type.equals(EntityType.SKELETON) || type.equals(EntityType.SILVERFISH) || type.equals(EntityType.HUSK)
        || type.equals(EntityType.DROWNED) || type.equals(EntityType.SLIME) || type.equals(EntityType.WITCH)) {

      Location loc = e.getLocation();
      World w = e.getEntity().getWorld();
      Entity specialZombie = w.spawnEntity(loc, EntityType.ZOMBIE);
      setRandomEffects(specialZombie);

      e.setCancelled(true);

    } else if (type.equals(EntityType.ZOMBIE)) {
      setRandomEffects(e.getEntity());
    } else
      e.setCancelled(false);
  }

  private void setRandomEffects(Entity entity) {
    entity.setFireTicks(0);

    if (!entity.getType().equals(EntityType.ZOMBIE))
      return;

    Zombie zombie = (Zombie) entity;
    Random r = new Random();
    int res = r.nextInt(5);

    switch (res) {
      case 0:
        zombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        zombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        zombie.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        zombie.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        zombie.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
        break;
      default:
        return;
    }
  }
}
