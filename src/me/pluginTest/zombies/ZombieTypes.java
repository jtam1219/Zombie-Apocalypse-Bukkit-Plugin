package me.pluginTest.zombies;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
  public void onMobSpawn(CreatureSpawnEvent e, EntityCombustEvent f) {
    EntityType type = e.getEntityType();
    if (type.equals(EntityType.CREEPER) || type.equals(EntityType.SPIDER) || type.equals(EntityType.CAVE_SPIDER)
        || type.equals(EntityType.SKELETON) || type.equals(EntityType.SILVERFISH) || type.equals(EntityType.HUSK)
        || type.equals(EntityType.DROWNED) || type.equals(EntityType.SLIME) || type.equals(EntityType.WITCH)) {

      Location loc = e.getLocation();
      World w = e.getEntity().getWorld();
      Entity specialZombie = w.spawnEntity(loc, EntityType.ZOMBIE);
      setRandomEffects(specialZombie);

      e.setCancelled(true);
      f.setCancelled(true);

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
    Material[] helmets={Material.LEATHER_HELMET,Material.CHAINMAIL_HELMET,
            Material.IRON_HELMET, Material.DIAMOND_HELMET,
            Material.GOLDEN_HELMET, Material.NETHERITE_HELMET};
    Material[] chestplates={Material.LEATHER_CHESTPLATE,
            Material.CHAINMAIL_CHESTPLATE,
            Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE,
            Material.GOLDEN_CHESTPLATE, Material.NETHERITE_CHESTPLATE};
    Material[] leggings={Material.LEATHER_LEGGINGS,
            Material.CHAINMAIL_LEGGINGS,
            Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS,
            Material.GOLDEN_LEGGINGS, Material.NETHERITE_LEGGINGS};
    Material[] boots={Material.LEATHER_BOOTS,
            Material.CHAINMAIL_BOOTS,
            Material.IRON_BOOTS, Material.DIAMOND_BOOTS,
            Material.GOLDEN_BOOTS, Material.NETHERITE_BOOTS};
    Material[] melee={Material.WOODEN_AXE,Material.WOODEN_SWORD,
            Material.STONE_AXE,Material.STONE_SWORD,Material.IRON_AXE,
            Material.IRON_SWORD,Material.DIAMOND_AXE, Material.DIAMOND_SWORD,
            Material.GOLDEN_AXE,Material.GOLDEN_SWORD, Material.NETHERITE_AXE
            , Material.NETHERITE_SWORD};
    int geared = r.nextInt(3);
    int effects= r.nextInt(3);
    int armor=r.nextInt(12);
    int weapon=r.nextInt(36);

    if (geared>0){
        if (armor<6) zombie.getEquipment().setHelmet(new ItemStack(helmets[armor]));
        armor=r.nextInt(12);
        if (armor<6) zombie.getEquipment().setChestplate(new ItemStack(chestplates[armor]));
        armor=r.nextInt(12);
        if (armor<6) zombie.getEquipment().setLeggings(new ItemStack(leggings[armor]));
        armor=r.nextInt(12);
        if (armor<6) zombie.getEquipment().setBoots(new ItemStack(boots[armor]));
        if (weapon<12) zombie.getEquipment().setItemInMainHand(new ItemStack(melee[weapon]));
    }

    switch (effects){
      case 0:
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,
                1000000, 3));
      case 1:
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,
                1000000, 3));
      default:
        break;
    }
  }
}
