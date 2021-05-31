package me.pluginTest.TheEndZombieBoss;

import me.pluginTest.Main;
import org.bukkit.*;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.DragonBattle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.event.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.World.Environment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class ZombieRaid implements Listener {
    private Main plugin;
    private boolean canLeave = true;
    private int illagerCount;

    public ZombieRaid(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void witherDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Wither
                && e.getEntity().getWorld().getEnvironment().equals(World.Environment.THE_END)
                && !(e.getEntity().getWorld().getEnderDragonBattle().hasBeenPreviouslyKilled())) {
            Location loc = e.getEntity().getLocation();
            World w = e.getEntity().getWorld();
            double x = loc.getBlockX(), y = loc.getBlockY(), z = loc.getBlockZ();
            for (int k = 0; k < 10; k++) {
                for (int i = 0; i < 10; i++) {
                    TNTPrimed explosion = (TNTPrimed) w.spawnEntity(new Location(w, x + i, y, z + k),
                            EntityType.PRIMED_TNT);
                    explosion.setYield(50);
                    explosion.setFuseTicks(200);
                    explosion.setIsIncendiary(false);
                }
            }
        }
    }

    @EventHandler
    public void poisonArrow(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Pillager && e.getEntity().hasMetadata("poisonPillager")) {
            e.setCancelled(true);
            Location start = e.getProjectile().getLocation();
            Vector direction = e.getProjectile().getVelocity();
            Arrow poison = start.getWorld().spawnArrow(start, direction, 1.5f, 8);
            poison.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 120, 1), true);

        }
    }

    @EventHandler
    public void stagePass(EntityDeathEvent e) {
        if ((e.getEntity() instanceof Illager || e.getEntity() instanceof Ravager)
                && e.getEntity().getWorld().getEnvironment().equals(World.Environment.THE_END))
            illagerCount--;
        World w;
        Location exitPortal;
        Location illagerSpawn;
        switch (illagerCount) {
            case 44:
                w = e.getEntity().getWorld();
                exitPortal = w.getEnderDragonBattle().getEndPortalLocation();
                illagerSpawn = new Location(w, exitPortal.getBlockX() - 5, exitPortal.getBlockY() + 2,
                        exitPortal.getBlockZ() - 5);
                illagerRaidStage2(w, illagerSpawn);
                break;
            case 36:
                w = e.getEntity().getWorld();
                exitPortal = w.getEnderDragonBattle().getEndPortalLocation();
                illagerSpawn = new Location(w, exitPortal.getBlockX() - 5, exitPortal.getBlockY() + 2,
                        exitPortal.getBlockZ() - 5);
                illagerRaidStage3(w, illagerSpawn);
                break;
            case 26:
                w = e.getEntity().getWorld();
                exitPortal = w.getEnderDragonBattle().getEndPortalLocation();
                illagerSpawn = new Location(w, exitPortal.getBlockX() - 5, exitPortal.getBlockY() + 2,
                        exitPortal.getBlockZ() - 5);
                illagerRaidStage4(w, illagerSpawn);
                break;
            case 15:
                w = e.getEntity().getWorld();
                exitPortal = w.getEnderDragonBattle().getEndPortalLocation();
                illagerSpawn = new Location(w, exitPortal.getBlockX() - 5, exitPortal.getBlockY() + 2,
                        exitPortal.getBlockZ() - 5);
                illagerRaidStage5(w, illagerSpawn);
                break;
            case 0:
                w = e.getEntity().getWorld();
                exitPortal = w.getEnderDragonBattle().getEndPortalLocation();
                illagerSpawn = new Location(w, exitPortal.getBlockX() - 5, exitPortal.getBlockY() + 2,
                        exitPortal.getBlockZ() - 5);
                Wither wither = (Wither) w.spawnEntity(illagerSpawn, EntityType.WITHER);
                finalBoss(w, illagerSpawn, wither);
                break;
            default:
                return;
        }
    }

    @EventHandler
    public void invincibleToFall(EntityDamageEvent e) {
        if (e.getEntity() instanceof Illager
                && e.getEntity().getWorld().getEnvironment().equals(World.Environment.THE_END)) {
            if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void stopSpawns(EntitySpawnEvent e) {
        if (e.getEntity().getWorld().getEnvironment().equals(World.Environment.THE_END)
                && e.getEntity() instanceof Enderman && !canLeave) {
            e.getEntity().remove();
        }
    }

    @EventHandler
    public void commencement(EntityDeathEvent e) {
        if (e.getEntity() instanceof EnderDragon
                && e.getEntity().getWorld().getEnvironment().equals(World.Environment.THE_END)
                && !(e.getEntity().getWorld().getEnderDragonBattle().hasBeenPreviouslyKilled())) {
            illagerCount = 50;
            // Location loc = e.getEntity().getLocation();
            World w = e.getEntity().getWorld();
            Location exitPortal = w.getEnderDragonBattle().getEndPortalLocation();
            Location illagerSpawn = new Location(w, exitPortal.getBlockX() - 5, exitPortal.getBlockY() + 2,
                    exitPortal.getBlockZ() - 5);
            e.getEntity().getWorld().getLivingEntities().forEach(entity -> {
                if (entity instanceof Monster) {
                    ((Monster) entity).damage(1000000);
                }
            });
            canLeave = false;
            blockPortal(w, w.getEnderDragonBattle().getEndPortalLocation());
            e.getEntity().getServer().broadcastMessage("Illagers have been "
                    + "discovered to be hiding in the End! They must be behind " + "the zombie outbreak. Defeat them!");
            illagerRaidStage1(w, illagerSpawn);
        }
    }

    public void finalBoss(World w, Location loc, Wither wither) {
        wither.getServer().broadcastMessage("You have breached the"
                + " core of the spread of the disease. Activating defensive" + " breach protocol.");
        blockPortal(w, w.getEnderDragonBattle().getEndPortalLocation());
        wither.getServer().broadcastMessage("[Wither]: Die.\n" + "[Core System]: Forcefield " + "activated.");
        wither.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,
                1000000, 29));
        wither.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 0));
        wither.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,
                1000000, 0));
        wither.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
                1000000, 2));
        BukkitTask checkStage = wither.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            int stage = 0;

            public void run() {

                if (wither.getAbsorptionAmount() <= 0 && (stage == 0)) {
                    wither.getServer().broadcastMessage(
                            "[Core " + "System]: Forcefield sustained" + " heavy damage, unleashing limiter stage 1.");
                    wither.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                    wither.removePotionEffect(PotionEffectType.SLOW);
                    wither.removePotionEffect(PotionEffectType.WEAKNESS);
                    wither.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
                            1000000, 1));
                    stage++;
                }
                if ((wither.getHealth() <= 225) && (stage == 1)) {
                    wither.getServer().broadcastMessage("[Core System]: Core " + "has sustained "
                            + "supplementary damage, unleashing limiter stage 2" + ".");
                    wither.getServer().broadcastMessage("[Core System]: " + "Wither has received Speed and Strength.");
                    wither.removePotionEffect(PotionEffectType.SLOW);
                    wither.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 0));
                    wither.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 0));
                    stage++;
                }
                if ((wither.getHealth() <= 150) && (stage == 2)) {
                    wither.getServer().broadcastMessage("[Core System]: Core "
                            + "has sustained medium damage, unleashing " + "limiter stage 3." + ".");
                    wither.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 0));
                    wither.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1000000, 0));
                    wither.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 1));
                    stage++;
                }
                if ((wither.getHealth() <= 75) && (stage == 3)) {
                    Location loc = wither.getLocation();
                    w.strikeLightningEffect(loc);
                    wither.getServer()
                            .broadcastMessage("[Core System]: " + "WARNING, CORE HAS SUSTAINED CRITICAL DAMAGE."
                                    + "REMOVING ALL LIMITERS. ACTIVATING CORE " + "REPAIR. ");
                    wither.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 2, 6));
                    wither.removePotionEffect(PotionEffectType.GLOWING);
                    wither.removePotionEffect(PotionEffectType.INVISIBILITY);
                    stage++;
                }
                if (wither.isDead() && (stage == 4)) {
                    wither.getServer().broadcastMessage("[Core System]: Core " + "has been destroyed." + " "
                            + "Releasing end portal escape. " + "Self-destruction in 10 seconds.");
                    BukkitTask selfDestruct = wither.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
                        int count = 10;

                        public void run() {
                            if (count != 0) {
                                wither.getServer().broadcastMessage("[Core System]:" + count);
                                count--;
                            } else {
                                Bukkit.getScheduler().cancelTask(wither.getMetadata("boom").get(0).asInt());
                            }
                        }
                    }, 0, 20);
                    wither.setMetadata("boom", new FixedMetadataValue(plugin, selfDestruct.getTaskId()));
                }
                if (wither.isDead()) {
                    Bukkit.getScheduler().cancelTask(wither.getMetadata("wither").get(0).asInt());
                    canLeave = true;
                    unblockPortal(w, w.getEnderDragonBattle().getEndPortalLocation());
                }
            }
        }, 0, 10);
        wither.setMetadata("wither", new FixedMetadataValue(plugin, checkStage.getTaskId()));

    }

    private void blockPortal(World w, Location loc) {
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.getWorld().getEnvironment().equals(World.Environment.THE_END) && Math.abs(player.getLocation().getBlockX()) <= 3 && Math.abs(player.getLocation().getBlockZ()) <= 3){
                player.teleport(new Location(player.getWorld(), 5, player.getLocation().getBlockY(), 5));
            }
        }
        int y_start = loc.getBlockY();
        for (int y = y_start + 1; y <= 255; y++) {
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    if (x == 0 && z == 0)
                        continue;
                    w.getBlockAt(x, y, z).setType(Material.BARRIER);
                }
            }
        }
    }

    private void unblockPortal(World w, Location loc) {
        int y_start = loc.getBlockY();
        for (int y = y_start + 1; y <= 255; y++) {
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    if (x == 0 && z == 0)
                        continue;
                    w.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }
    }

    public void illagerRaidStage1(World w, Location loc) {
        for (int i = 0; i < 3; i++) {
            Pillager pillager=(Pillager)(w.spawnEntity(loc,
                    EntityType.PILLAGER));
            pillager.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200);
            ((LivingEntity)pillager).setRemoveWhenFarAway(false);
        }
        for (int i = 0; i < 3; i++) {
            Vindicator vindicator=(Vindicator) w.spawnEntity(loc,
                    EntityType.VINDICATOR);
            vindicator.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200);
            ((LivingEntity)vindicator).setRemoveWhenFarAway(false);
        }
    }

    public void illagerRaidStage2(World w, Location loc) {
        for (int i = 0; i < 5; i++) {
            Pillager pillager=(Pillager) w.spawnEntity(loc,
                    EntityType.PILLAGER);
            pillager.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200);
            ((LivingEntity)pillager).setRemoveWhenFarAway(false);

        }
        for (int i = 0; i < 3; i++) {
            ItemStack[] gear = { new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.LEATHER_CHESTPLATE),
                    new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_BOOTS) };
            LeatherArmorMeta color1 = (LeatherArmorMeta) gear[0].getItemMeta();
            LeatherArmorMeta color2 = (LeatherArmorMeta) gear[1].getItemMeta();
            LeatherArmorMeta color3 = (LeatherArmorMeta) gear[2].getItemMeta();
            LeatherArmorMeta color4 = (LeatherArmorMeta) gear[3].getItemMeta();
            color1.setColor(Color.BLUE);
            color2.setColor(Color.BLUE);
            color3.setColor(Color.BLUE);
            color4.setColor(Color.BLUE);
            gear[0].setItemMeta(color1);
            gear[1].setItemMeta(color2);
            gear[2].setItemMeta(color3);
            gear[3].setItemMeta(color4);
            Vindicator vindicator = (Vindicator) w.spawnEntity(loc, EntityType.VINDICATOR);
            vindicator.getEquipment().setHelmet(gear[0]);
            vindicator.getEquipment().setChestplate(gear[1]);
            vindicator.getEquipment().setLeggings(gear[2]);
            vindicator.getEquipment().setBoots(gear[3]);
            vindicator.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200);
            ((LivingEntity)vindicator).setRemoveWhenFarAway(false);
        }
    }

    public void illagerRaidStage3(World w, Location loc) {
        for (int i = 0; i < 1; i++) {
            Evoker evoker= (Evoker) w.spawnEntity(loc, EntityType.EVOKER);
            evoker.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200);
            ((LivingEntity)evoker).setRemoveWhenFarAway(false);
        }
        for (int i = 0; i < 5; i++) {
            Pillager pillager = (Pillager) w.spawnEntity(loc, EntityType.PILLAGER);
            ItemStack crossBow = new ItemStack(Material.CROSSBOW);
            crossBow.addEnchantment(Enchantment.MULTISHOT, 1);
            pillager.getEquipment().setItemInMainHand(crossBow);
            pillager.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200);
            ((LivingEntity)pillager).setRemoveWhenFarAway(false);
        }
        for (int i = 0; i < 3; i++) {
            Vindicator vindicator=(Vindicator) w.spawnEntity(loc,
                    EntityType.VINDICATOR);
            vindicator.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200);
            ((LivingEntity)vindicator).setRemoveWhenFarAway(false);
        }
        for (int i = 0; i < 1; i++) {
            Ravager ravager=(Ravager) w.spawnEntity(loc, EntityType.RAVAGER);
            ravager.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200);
            ((LivingEntity)ravager).setRemoveWhenFarAway(false);
        }
    }

    public void illagerRaidStage4(World w, Location loc) {
        for (int i = 0; i < 2; i++) {
            Evoker evoker= (Evoker) w.spawnEntity(loc, EntityType.EVOKER);
            evoker.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200);
            ((LivingEntity)evoker).setRemoveWhenFarAway(false);
        }
        for (int i = 0; i < 1; i++) {
            Illusioner illusioner= (Illusioner) w.spawnEntity(loc,
                    EntityType.ILLUSIONER);
            illusioner.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200);
            ((LivingEntity)illusioner).setRemoveWhenFarAway(false);
        }
        for (int i = 0; i < 3; i++) {
            Pillager pillager = (Pillager) w.spawnEntity(loc, EntityType.PILLAGER);
            pillager.setMetadata("poisonPillager", new FixedMetadataValue(plugin, "poisonPillager"));
            ItemStack poisonBow = new ItemStack(Material.CROSSBOW);
            poisonBow.addEnchantment(Enchantment.MULTISHOT, 1);
            ItemStack[] gear = { new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.LEATHER_CHESTPLATE),
                    new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_BOOTS) };
            LeatherArmorMeta color1 = (LeatherArmorMeta) gear[0].getItemMeta();
            LeatherArmorMeta color2 = (LeatherArmorMeta) gear[1].getItemMeta();
            LeatherArmorMeta color3 = (LeatherArmorMeta) gear[2].getItemMeta();
            LeatherArmorMeta color4 = (LeatherArmorMeta) gear[3].getItemMeta();
            color1.setColor(Color.BLUE);
            color2.setColor(Color.BLUE);
            color3.setColor(Color.BLUE);
            color4.setColor(Color.BLUE);
            gear[0].setItemMeta(color1);
            gear[1].setItemMeta(color2);
            gear[2].setItemMeta(color3);
            gear[3].setItemMeta(color4);
            pillager.getEquipment().setHelmet(gear[0]);
            pillager.getEquipment().setChestplate(gear[1]);
            pillager.getEquipment().setLeggings(gear[2]);
            pillager.getEquipment().setBoots(gear[3]);
            pillager.getEquipment().setItemInMainHand(poisonBow);
            pillager.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200);
            ((LivingEntity)pillager).setRemoveWhenFarAway(false);
        }
        for (int i = 0; i < 5; i++) {
            ItemStack[] gear = { new ItemStack(Material.CHAINMAIL_HELMET), new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                    new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.CHAINMAIL_BOOTS) };
            Vindicator vindicator = (Vindicator) w.spawnEntity(loc, EntityType.VINDICATOR);
            vindicator.getEquipment().setHelmet(gear[0]);
            vindicator.getEquipment().setChestplate(gear[1]);
            vindicator.getEquipment().setLeggings(gear[2]);
            vindicator.getEquipment().setBoots(gear[3]);
            vindicator.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200);
            ((LivingEntity)vindicator).setRemoveWhenFarAway(false);
        }
    }

    public void illagerRaidStage5(World w, Location loc) {
        for (int i = 0; i < 2; i++) {
            Illusioner illusioner= (Illusioner) w.spawnEntity(loc,
                    EntityType.ILLUSIONER);
            illusioner.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200);
            ((LivingEntity)illusioner).setRemoveWhenFarAway(false);
        }
        for (int i = 0; i < 3; i++) {
            Evoker evoker= (Evoker) w.spawnEntity(loc, EntityType.EVOKER);
            evoker.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200);
            ((LivingEntity)evoker).setRemoveWhenFarAway(false);
        }
        for (int i = 0; i < 2; i++) {
            Ravager ravager=(Ravager) w.spawnEntity(loc, EntityType.RAVAGER);
            ravager.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200);
            ((LivingEntity)ravager).setRemoveWhenFarAway(false);
        }
        for (int i = 0; i < 5; i++) {
            Vindicator vindicator = (Vindicator) w.spawnEntity(loc, EntityType.VINDICATOR);
            ItemStack[] gear = { new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.IRON_CHESTPLATE),
                    new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_BOOTS) };
            LeatherArmorMeta color1 = (LeatherArmorMeta) gear[0].getItemMeta();
            color1.setColor(Color.BLUE);
            gear[0].setItemMeta(color1);
            vindicator.getEquipment().setHelmet(gear[0]);
            vindicator.getEquipment().setChestplate(gear[1]);
            vindicator.getEquipment().setLeggings(gear[2]);
            vindicator.getEquipment().setBoots(gear[3]);
            vindicator.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200);
            ((LivingEntity)vindicator).setRemoveWhenFarAway(false);
        }
        for (int i = 0; i < 3; i++) {
            Pillager pillager = (Pillager) w.spawnEntity(loc, EntityType.PILLAGER);
            pillager.setMetadata("poisonPillager", new FixedMetadataValue(plugin, "poisonPillager"));
            ItemStack poisonBow = new ItemStack(Material.CROSSBOW);
            poisonBow.addEnchantment(Enchantment.MULTISHOT, 1);
            poisonBow.addEnchantment(Enchantment.QUICK_CHARGE, 3);
            ItemStack[] gear = { new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                    new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_BOOTS) };
            LeatherArmorMeta color1 = (LeatherArmorMeta) gear[0].getItemMeta();
            LeatherArmorMeta color3 = (LeatherArmorMeta) gear[2].getItemMeta();
            LeatherArmorMeta color4 = (LeatherArmorMeta) gear[3].getItemMeta();
            color1.setColor(Color.BLUE);
            color3.setColor(Color.BLUE);
            color4.setColor(Color.BLUE);
            gear[0].setItemMeta(color1);
            gear[2].setItemMeta(color3);
            gear[3].setItemMeta(color4);
            pillager.getEquipment().setHelmet(gear[0]);
            pillager.getEquipment().setChestplate(gear[1]);
            pillager.getEquipment().setLeggings(gear[2]);
            pillager.getEquipment().setBoots(gear[3]);
            pillager.getEquipment().setItemInMainHand(poisonBow);
            pillager.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(200);
            ((LivingEntity)pillager).setRemoveWhenFarAway(false);
        }
    }
}
