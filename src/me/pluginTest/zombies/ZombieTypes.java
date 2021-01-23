package me.pluginTest.zombies;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.pluginTest.Main;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class ZombieTypes implements Listener {
  private Main plugin;

  public ZombieTypes(Main plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onMobSpawn(CreatureSpawnEvent e) {
    try {
      EntityType type = e.getEntityType();
      World w = e.getEntity().getWorld();
      if (w.getEnvironment().equals(World.Environment.NORMAL)) {
        if (type.equals(EntityType.CREEPER) || type.equals(EntityType.SPIDER) || type.equals(EntityType.CAVE_SPIDER)
            || type.equals(EntityType.SKELETON) || type.equals(EntityType.SILVERFISH) || type.equals(EntityType.HUSK)
            || type.equals(EntityType.SLIME) || type.equals(EntityType.WITCH) || type.equals(EntityType.ZOMBIE)) {
          if (e.getSpawnReason().equals(SpawnReason.CUSTOM) || e.getSpawnReason().equals(SpawnReason.DEFAULT))
            return;
          Location loc = e.getLocation();
          e.getEntity().remove();
          createSpecialZombie(w, loc);

        } else if (type.equals(EntityType.DROWNED)) {
          if (e.getSpawnReason().equals(SpawnReason.DROWNED)) {
            e.getEntity().remove();
          }
        }
        // else if (type.equals(EntityType.PHANTOM)) {
        // Location loc = e.getLocation();
        // Entity riderZombie = createSpecialZombie(w, loc);
        // e.getEntity().addPassenger(riderZombie);
        // }
      }
    } catch (Exception ex) {
      System.out.println("Error creating a special Zombie");
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onEntityCombust(EntityCombustEvent e) {
    if (e.getEntity() instanceof Zombie) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onDeath(EntityDeathEvent e) {
    if (e.getEntity() instanceof Zombie && e.getEntity().hasMetadata("Boomer")) {
      Location loc = e.getEntity().getLocation();
      World w = e.getEntity().getWorld();
      TNTPrimed explosion = (TNTPrimed) w.spawnEntity(loc, EntityType.PRIMED_TNT);
      explosion.setYield(3);
      explosion.setFuseTicks(30);
      explosion.setIsIncendiary(false);
    }
  }

  @EventHandler
  public void leech(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof Zombie && e.getDamager().hasMetadata("Leech")) {
      e.getEntity().addPassenger(e.getDamager());
    }
  }

  @EventHandler
  public void grabAndToss(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof Zombie && e.getDamager().hasMetadata("Wailer")) {
      e.setDamage(0);
      if (e.getEntity().getVehicle() != null)
        return;
      e.getDamager().addPassenger(e.getEntity());
      ((Zombie) e.getDamager()).setAI(false);
      e.getEntity().getServer().getScheduler().runTaskLater(plugin, new Runnable() {
        public void run() {
          ((Zombie) e.getDamager()).setAI(true);
          Vector directionVector = e.getDamager().getLocation().getDirection();
          e.getDamager().removePassenger(e.getEntity());
          directionVector = directionVector.multiply(new Vector(1, 0, 1)).normalize().multiply(1.5);
          e.getEntity().setVelocity(directionVector.add(new Vector(0, 1.25, 0)));
        }
      }, 15);
    }
  }

  @EventHandler
  public void tankHealthChecker(EntityDamageEvent e) {
    if (e.getEntity() instanceof Zombie && e.getEntity().hasMetadata("Tank")) {
      Zombie tank = (Zombie) e.getEntity();
       //if (!tank.hasPotionEffect(PotionEffectType.ABSORPTION)) {
       //tank.getServer().broadcastMessage("Health: " + (tank.getHealth() -
       //e.getDamage()));
      //} else {
       tank.getServer().broadcastMessage("Health: " + tank.getHealth());
       //}
    }
  }

  @EventHandler
  public void bulwarkHealthChecker(EntityDamageEvent e) {
    if (e.getEntity() instanceof Zombie && e.getEntity().hasMetadata("bulwark")) {
      Zombie bulwark = (Zombie) e.getEntity();
      //if (!bulwark.hasPotionEffect(PotionEffectType.ABSORPTION)) {
       // bulwark.getServer().broadcastMessage("Health: " + (bulwark
      // .getHealth() -
       //         e.getDamage()));
      //} else {
        bulwark.getServer().broadcastMessage("Health: " +bulwark.getHealth());
      //}
    }
  }


  @EventHandler
  public void tankFallDamage(EntityDamageEvent e) {
    if (e.getEntity() instanceof Zombie && e.getEntity().hasMetadata("Tank")) {
      if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
        e.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void jumperCalculation(EntityDamageEvent e) {
    if (e.getEntity() instanceof Zombie && e.getEntity().hasMetadata("Jumper")) {
      if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
        Zombie zombie = (Zombie) e.getEntity();
        AttributeInstance damage = zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(damage.getBaseValue() + (e.getDamage() / 2));
      }
    }
  }

  @EventHandler
  public void noSleep(PlayerBedEnterEvent e) {
    e.setCancelled(true);
    e.getPlayer().sendMessage("NO SLEEPING!");
  }

  @EventHandler
  public void dropPearl(EntityDeathEvent e) {
    if (e.getEntity() instanceof Enderman) {
      e.getDrops().add(new ItemStack(Material.ENDER_PEARL));
    }
  }

  @EventHandler
  public void changeTarget(EntityDamageByEntityEvent e) {
    if (e.getEntity() instanceof Zombie) {
      Player newTarget = null;
      Entity damager = e.getDamager();
      if (damager instanceof Projectile) {
        Projectile proj = (Projectile) damager;
        if (proj.getShooter() instanceof Player) {
          newTarget = (Player) proj.getShooter();
        }
      } else if (damager instanceof Player) {
        newTarget = (Player) damager;
      }
      if (newTarget != null && (!(newTarget.getGameMode().equals(GameMode.CREATIVE)))) {
        ((Zombie) e.getEntity()).setTarget(newTarget);
      }
    }
  }

  public Entity createSpecialZombie(World w, Location loc) {

    Random r = new Random();
    int geared = r.nextInt(10);
    int effects = r.nextInt(50);
    int armor = r.nextInt(10);
    int weapon = r.nextInt(12);

    Entity specialEntity;

    Material[] helmets = { Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.GOLDEN_HELMET };
    Material[] chestplates = { Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.GOLDEN_CHESTPLATE };
    Material[] leggings = { Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.GOLDEN_LEGGINGS };
    Material[] boots = { Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.GOLDEN_BOOTS };
    Material[] melee = { Material.WOODEN_AXE, Material.WOODEN_SWORD, Material.STONE_AXE, Material.STONE_SWORD,
        Material.IRON_AXE, Material.IRON_SWORD };

    if (effects <= 22 || effects > 30) {
      specialEntity = w.spawnEntity(loc, EntityType.ZOMBIE);
      Zombie zombie = (Zombie) specialEntity;
      if (effects >= 0 && effects <= 2) {
        zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(50);
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 3));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000, 0));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1000000, 4));
        // zombie.setCustomName("Jumper");
        zombie.setMetadata("DamageAddOn", new FixedMetadataValue(plugin, "Jumper"));
        BukkitTask isOnGround = zombie.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
          public void run() {
            if (zombie.isDead()) {
              Bukkit.getScheduler().cancelTask(zombie.getMetadata("Jumper").get(0).asInt());
              // zombie.getServer().broadcastMessage("Zombie " + zombie.getEntityId() + "
              // dead. climbCycle cancelled");
            }
            if (!zombie.isOnGround())
              zombie.removePotionEffect(PotionEffectType.JUMP);
            else if (!zombie.hasPotionEffect(PotionEffectType.JUMP))
              zombie.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 3));
          }
        }, 5, 5);
        zombie.setMetadata("Jumper", new FixedMetadataValue(plugin, isOnGround.getTaskId()));
        // Jumper height calculates more damage
      }
      if (effects >= 3 && effects <= 10) {
        zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(50);
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 2));
        // zombie.setCustomName("Zoomer");
        // Normal Faster Zombie
      }
      if (effects >= 11 && effects <= 14) {
        zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(70);
        zombie.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(.5);
        zombie.setHealth(4);
        zombie.getEquipment().setHelmet(new ItemStack(Material.TNT));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 0));
        zombie.setMetadata("Boomer", new FixedMetadataValue(plugin, "Boomer"));
        // Explodes upon Death
        // zombie.setCustomName("Boomer");
      }
      if (effects == 15 || effects == 16) {
        zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(5);
        zombie.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(.25);
        zombie.getEquipment().setHelmet(new ItemStack(Material.CREEPER_HEAD));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1000000, 19));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 2));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 3));
        return specialEntity;
        // Speedy, and lethal
        // zombie.setCustomName("Witch");
      }
      if (effects >= 17 && effects <= 19) {
        zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(50);
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 0));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1000000, 2));
        // zombie.setCustomName("Wailer");
        // use metadata grabandtoss, Grabs and hurls you to death : P
        zombie.setMetadata("Wailer", new FixedMetadataValue(plugin, "Wailer"));
      }
      if (effects >= 20 && effects <= 22) {
        zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(50);
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 1));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1000000, 2));
        // zombie.setCustomName("Leech");
        zombie.setMetadata("Leech", new FixedMetadataValue(plugin, "Leech"));
        // use metadata leech, rides you and hits you on your head
      }
      if (geared > 4) {
        if (geared <= 7) {
          if (armor < 3)
            zombie.getEquipment().setHelmet(new ItemStack(helmets[armor]));
          armor = r.nextInt(10);
          if (armor < 3)
            zombie.getEquipment().setChestplate(new ItemStack(chestplates[armor]));
          armor = r.nextInt(10);
          if (armor < 3)
            zombie.getEquipment().setLeggings(new ItemStack(leggings[armor]));
          armor = r.nextInt(10);
          if (armor < 3)
            zombie.getEquipment().setBoots(new ItemStack(boots[armor]));
          if (weapon < 6)
            zombie.getEquipment().setItemInMainHand(new ItemStack(melee[weapon]));
        } else {
          ItemStack bulwarkPlate = new ItemStack(Material.DIAMOND_CHESTPLATE);
          bulwarkPlate.addEnchantment(Enchantment.PROTECTION_FIRE, 4);
          bulwarkPlate.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 4);
          bulwarkPlate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
          ItemStack tankPlate = new ItemStack(Material.NETHERITE_CHESTPLATE);
          tankPlate.addEnchantment(Enchantment.DURABILITY, 3);
          ItemStack tankBoot = new ItemStack(Material.NETHERITE_BOOTS);
          tankBoot.addEnchantment(Enchantment.DURABILITY, 3);
          int num = r.nextInt(200);
          if (num < 15) {
            zombie.setAdult();
            zombie.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
            zombie.getEquipment().setChestplate(tankPlate);
            zombie.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            zombie.getEquipment().setBoots(tankBoot);
          } else if (num > 15 && num <= 25) {
            zombie.setAdult();
            zombie.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
            zombie.getEquipment().setChestplate(bulwarkPlate);
            zombie.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            zombie.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
            zombie.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_AXE));
            zombie.getEquipment().setItemInOffHand(new ItemStack(Material.SHIELD));
          } else {
            if (armor < 3) {
              zombie.getEquipment().setHelmet(new ItemStack(helmets[armor]));
              zombie.getEquipment().setChestplate(new ItemStack(chestplates[armor]));
              zombie.getEquipment().setLeggings(new ItemStack(leggings[armor]));
              zombie.getEquipment().setBoots(new ItemStack(boots[armor]));
              if (weapon < 6)
                zombie.getEquipment().setItemInMainHand(new ItemStack(melee[weapon]));
              zombie.getEquipment().setHelmetDropChance(0.02f);
              zombie.getEquipment().setChestplateDropChance(0.02f);
              zombie.getEquipment().setLeggingsDropChance(0.02f);
              zombie.getEquipment().setBootsDropChance(0.02f);
              zombie.getEquipment().setItemInMainHandDropChance(0.02f);
            }
          }
          if (zombie.getEquipment().getChestplate().equals(tankPlate)) {
            // entity.getServer().broadcastMessage("A Tank has been spawned!");
            zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(9);
            zombie.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(5);
            zombie.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(.75);
            zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(35);
            zombie.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(.5);
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST,
                    1000000, 4));
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 2,
                    6));
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 2));
            zombie.getEquipment().setHelmetDropChance(0.15f);
            zombie.getEquipment().setChestplateDropChance(0.05f);
            zombie.getEquipment().setLeggingsDropChance(0.1f);
            zombie.getEquipment().setBootsDropChance(0.05f);
            // zombie.setCustomName("Tank");
            zombie.setMetadata("Tank", new FixedMetadataValue(plugin, "Tank"));
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
                        } else if (zombie.getLocation().add(new Vector(x, 0, z)).getBlock().getType().isSolid()) {
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
          } else if (zombie.getEquipment().getChestplate().equals(bulwarkPlate)) {
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
                    1000000, 9));
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 2,
                    6));
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 0));
            zombie.setMetadata("bulwark", new FixedMetadataValue(plugin, "bulwark"));
          }
        }
      }
    } else {
      specialEntity = w.spawnEntity(loc, EntityType.DROWNED);
      Drowned drowned = (Drowned) specialEntity;
      if (effects <= 28) {
        drowned.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 1000000, 1));
        drowned.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1000000, 4));
        drowned.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(50);
        // drowned.setCustomName("Drowner");
      }
      if (effects == 29 || effects == 30) {
        // Special Zombie with land and water capabilities, from the water.
        // One that is adept at both the ocean and the land. (Entity.DROWNED)
        drowned.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 1));
        drowned.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 1000000, 0));
        drowned.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 1000000, 1));
        drowned.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1000000, 4));
        ItemStack drownedBoots = new ItemStack(Material.DIAMOND_BOOTS);
        ItemStack drownedChestPlate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        drownedChestPlate.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 2);
        drownedBoots.addEnchantment(Enchantment.DEPTH_STRIDER, 3);
        drowned.getEquipment().setBoots(drownedBoots);
        drowned.getEquipment().setChestplate(drownedChestPlate);
        drowned.getEquipment().setItemInMainHand(new ItemStack(Material.TRIDENT));
        drowned.getEquipment().setBootsDropChance(0.01f);
        drowned.getEquipment().setChestplateDropChance(0.005f);
        drowned.getEquipment().setItemInMainHandDropChance(0.03f);
        drowned.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(70);
        // drowned.setCustomName("Swirler");
      }
    }
    return specialEntity;
  }
}
