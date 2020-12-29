package me.pluginTest;

import me.pluginTest.commands.ZombieCommand;
import me.pluginTest.zombies.ZombieTypes;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ZombieTypes(), this);
        new ZombieCommand(this);

    }
}
