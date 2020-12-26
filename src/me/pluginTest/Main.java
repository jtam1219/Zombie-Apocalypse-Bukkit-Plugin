package me.pluginTest;

import me.pluginTest.commands.ZombieCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        new ZombieCommand(this);
    }
}
