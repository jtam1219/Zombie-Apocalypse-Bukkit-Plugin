package me.pluginTest.TheEndZombieBoss;

import me.pluginTest.Main;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.event.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;

public class ZombieRaid implements Listener {
    private Main plugin;
    private boolean canLeave = true;

    public ZombieRaid(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Wither) {
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
    public void stopSpawns(EntitySpawnEvent e) {
        if (e.getEntity().getWorld().getEnvironment().equals(World.Environment.THE_END)
                && e.getEntity() instanceof Monster && !canLeave) {
            e.getEntity().remove();
        }
    }

    @EventHandler
    public void finalBoss(EntityDeathEvent e) {
        if (e.getEntity() instanceof EnderDragon) {
            Location loc = e.getEntity().getLocation();
            World w = e.getEntity().getWorld();
            e.getEntity().getServer().broadcastMessage("You have breached the"
                    + " core of the spread of the disease. Activating defensive" + " breach protocol.");

            e.getEntity().getWorld().getLivingEntities().forEach(entity -> {
                if (entity instanceof Monster) {
                    ((Monster) entity).damage(1000000);
                }
            });
            Wither wither = (Wither) w.spawnEntity(loc, EntityType.WITHER);
            canLeave = false;
            blockPortal(w, w.getEnderDragonBattle().getEndPortalLocation());
            wither.getServer().broadcastMessage("[Wither]: Die.\n" + "[Core System]: Forcefield " + "activated.");
            wither.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1000000, 49));
            wither.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 1));
            wither.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1000000, 0));
            wither.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 1));
            BukkitTask checkStage = wither.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
                int stage = 0;

                public void run() {

                    if (wither.getAbsorptionAmount() <= 0 && (stage == 0)) {
                        wither.getServer().broadcastMessage("[Core " + "System]: Forcefield sustained"
                                + " heavy damage, unleashing limiter stage 1.");
                        wither.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                        wither.removePotionEffect(PotionEffectType.SLOW);
                        wither.removePotionEffect(PotionEffectType.WEAKNESS);
                        stage++;
                    }
                    if ((wither.getHealth() <= 225) && (stage == 1)) {
                        wither.getServer().broadcastMessage("[Core System]: Core " + "has sustained "
                                + "supplementary damage, unleashing limiter stage 2" + ".");
                        wither.getServer()
                                .broadcastMessage("[Core System]: " + "Wither has received Speed and Strength.");
                        wither.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 0));
                        wither.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 0));
                        stage++;
                    }
                    if ((wither.getHealth() <= 150) && (stage == 2)) {
                        wither.getServer().broadcastMessage("[Core System]: Core "
                                + "has sustained medium damage. Activating " + "defensive-limiter protocols.");
                        wither.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 0));
                        wither.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1000000, 0));
                        wither.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 1));
                        stage++;
                    }
                    if ((wither.getHealth() <= 75) && (stage == 3)) {
                        Location loc=wither.getLocation();
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
                        BukkitTask selfDestruct = wither.getServer().getScheduler().runTaskTimer(plugin,
                                new Runnable() {
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
    }

    private void blockPortal(World w, Location loc) {
        int y = loc.getBlockY() + 1;
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (x == 0 && z == 0)
                    continue;
                w.getBlockAt(x, y, z).setType(Material.BARRIER);
            }
        }
    }

    private void unblockPortal(World w, Location loc) {
        int y = loc.getBlockY() + 1;
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (x == 0 && z == 0)
                    continue;
                w.getBlockAt(x, y, z).setType(Material.AIR);
            }
        }
    }
}
