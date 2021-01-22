package me.pluginTest.TheEndZombieBoss;

import me.pluginTest.Main;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.event.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;

public class ZombieRaid implements Listener {
    private Main plugin;
    private boolean canLeave=true;

    public ZombieRaid(Main plugin){this.plugin=plugin;}

    @EventHandler
    public void deniedLeave(PlayerPortalEvent e){
        if (e.getPlayer().getWorld().getEnvironment()== World.Environment.THE_END){
            if (!canLeave){
                e.setCancelled(true);
            }
        }
    }
    public void finalBoss(EntityDeathEvent e){
        EntityType dragoon=e.getEntityType();
        if (dragoon==EntityType.ENDER_DRAGON){
            Location loc=e.getEntity().getLocation();
            World w=e.getEntity().getWorld();
            e.getEntity().getServer().broadcastMessage("You have breached the" +
                    " core of the spread of the disease. Activating defensive" +
                            " breach protocol." );

            Wither wither=(Wither) w.spawnEntity(loc,EntityType.WITHER);
            wither.getServer().broadcastMessage("[Wither]: Die.\n " +
                    "[Core System]: Forcefield " +
                    "activated.");
            wither.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1000000, 24));
            wither.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 1));
            wither.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS
                    , 1000000, 0));
            wither.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
                    1000000, 0));
            canLeave=false;
            BukkitTask checkStage =
                    wither.getServer().getScheduler().runTaskTimer(plugin,
                            new Runnable() {
                                int stage=0;
                                public void run() {
                                    if (wither.isDead()) {
                                        canLeave=true;
                                        Bukkit.getScheduler().cancelTask(wither.getMetadata("wither").get(0).asInt());
                                    }
                                    if (!wither.hasPotionEffect(PotionEffectType.ABSORPTION) && (stage==0)) {
                                        wither.getServer().broadcastMessage("[Core " +
                                                "System]: Forcefield sustained" +
                                                " heavy damage, unleashing limiter stage 1.");
                                        wither.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                                        wither.removePotionEffect(PotionEffectType.SLOW);
                                        wither.removePotionEffect(PotionEffectType.WEAKNESS);
                                        stage++;
                                    }
                                    if ((wither.getHealth()<=200)&&(wither.getHealth()>100)&&(stage==1)){
                                        wither.getServer().broadcastMessage("[Core System]: Core " +
                                                "has sustained " +
                                                "supplementary damage, unleashing limiter stage 2" +
                                                ".");
                                        wither.getServer().broadcastMessage("[Core System]: " +
                                                "Wither has received Speed and Strength.");
                                        wither.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 0));
                                        wither.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 0));
                                        stage++;
                                    }
                                    if ((wither.getHealth()<=100) && (stage==2)){
                                        wither.getServer().broadcastMessage("[Core System]: Core " +
                                                "has sustained heavy damage. Activating " +
                                                "last resort protocols.");
                                        wither.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 0));
                                        wither.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1000000, 0));
                                        wither.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 2));
                                        stage++;
                                    }
                                    if ((wither.getHealth()<=0) && (stage==3)){
                                        wither.getServer().broadcastMessage("[Core System]: Core " +
                                                "has been destroyed. " +
                                                "Releasing end portal escape.");
                                    }
                                }
                            },5,5);
            wither.setMetadata("wither",
                    new FixedMetadataValue(plugin, checkStage.getTaskId())
                    );



        }
    }
}
