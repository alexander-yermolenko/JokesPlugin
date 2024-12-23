package jokesPlugin;

import jokesPlugin.commands.JokeCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class JokesPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("JokesPlugin is running!");

        // Creates the config file if it doesn't exist
        saveDefaultConfig();

        // Commands
        Objects.requireNonNull(getCommand("joke")).setExecutor(new JokeCommand(this));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("JokesPlugin is running!");
    }
}