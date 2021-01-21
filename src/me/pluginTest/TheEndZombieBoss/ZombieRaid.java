package me.pluginTest.TheEndZombieBoss;

import me.pluginTest.Main;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
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

public class ZombieRaid implements Listener {
    private Main plugin;

    public ZombieRaid(Main plugin){this.plugin=plugin;}

    public void finalBoss(EntityDeathEvent e){
        EntityType dragoon=e.getEntityType();
        if (dragoon==EntityType.ENDER_DRAGON){
            Location loc=e.getEntity().getLocation();
            World w=e.getEntity().getWorld();
            e.getEntity().getServer().broadcastMessage("You have breached the" +
                    " core of the spread of the disease. Activating defensive" +
                            " breach protocol." );
            Entity wither=w.spawnEntity(loc,EntityType.WITHER);
            wither.getServer().broadcastMessage("Wither: Die.");

        }
    }
}
