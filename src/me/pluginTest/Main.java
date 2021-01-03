package me.pluginTest;

import me.pluginTest.commands.HordeSpawn;
import me.pluginTest.commands.ZombieCommand;
import me.pluginTest.nether.empoweredMobs;
import me.pluginTest.zombies.SpawnJumper;
import me.pluginTest.zombies.SpawnLeech;
import me.pluginTest.zombies.SpawnWailer;
import me.pluginTest.zombies.ZombieTypes;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ZombieTypes(this), this);
        getServer().getPluginManager().registerEvents(new empoweredMobs(this)
                , this);
        new ZombieCommand(this);
        new HordeSpawn(this);
        new SpawnJumper(this);
        new SpawnWailer(this);
        new SpawnLeech(this);
    }
}
