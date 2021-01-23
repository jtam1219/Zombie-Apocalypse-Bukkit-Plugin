package me.pluginTest;

import me.pluginTest.TheEndZombieBoss.ZombieRaid;
import me.pluginTest.commands.*;
import me.pluginTest.nether.empoweredMobs;
import me.pluginTest.zombies.ZombieTypes;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ZombieTypes(this), this);
        getServer().getPluginManager().registerEvents(new empoweredMobs(this), this);
        getServer().getPluginManager().registerEvents(new ZombieRaid(this),
                this);
        new ZombieCommand(this);
        new HordeSpawn(this);
        new SpawnJumper(this);
        new SpawnWailer(this);
        new SpawnGiantTest(this);
        new SpawnLeech(this);
        new SpawnBulwark(this);
    }
}
