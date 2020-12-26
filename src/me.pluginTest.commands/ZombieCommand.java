package me.pluginTest.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.pluginTest.Main;

public class ZombieCommand implements CommandExecutor{
    private Main plugin;

    public ZombieCommand(Main plugin){
        this.plugin=plugin;
        plugin.getCommand()
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lable,
                             String[] args){

    }

}