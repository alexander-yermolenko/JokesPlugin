package jokesPlugin.commands;

import jokesPlugin.JokesPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class JokeCommand implements CommandExecutor {

    private final JokesPlugin plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public JokeCommand(JokesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getLogger().info(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("joke")) {
            if (args.length == 0) {
                handleJokeCommand(player);
            } else if (args[0].equalsIgnoreCase("add")) {
                handleAddCommand(player, args);
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /joke or /joke add <your joke>");
            }
            return true;
        }

        return false;
    }

    private void handleJokeCommand(Player player) {
        // Get cooldown time from the config
        int cooldownSeconds = plugin.getConfig().getInt("cooldown-seconds");

        // Default cooldown time isn't set up
        if (plugin.getConfig().get("cooldown-seconds") == null) {
            cooldownSeconds = 30; // Default cooldown time is 30
        }

        if (isOnCooldown(player, cooldownSeconds)) {
            long timeLeft = getTimeLeft(player, cooldownSeconds);
            player.sendMessage(ChatColor.YELLOW + "You must wait " + timeLeft + " seconds before using this command again.");
            return;
        }

        String joke = getRandomJoke();
        if (joke != null) {
            player.sendMessage(ChatColor.GREEN + "Here's a joke for you: " + joke);
            playSoundFromConfig(player, "sounds.joke");
            setCooldown(player);
        } else {
            player.sendMessage(ChatColor.RED + "No jokes found in the configuration!");
        }
    }

    private void handleAddCommand(Player player, String[] args) {
        if (!player.hasPermission("jokesPlugin.commands.add")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to add jokes.");
            return;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /joke add <your joke>");
            return;
        }

        String newJoke = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        // Check joke length limit if you want to enforce it
        int maxLength = plugin.getConfig().getInt("max-joke-length");

        // Default joke length isn't set up
        if (plugin.getConfig().get("max-joke-length") == null) {
            maxLength = 150; // Default joke length is 150
        }

        if (newJoke.length() > maxLength) {
            player.sendMessage(ChatColor.RED + "Your joke is too long! Maximum length is " + maxLength + " characters.");
            return;
        }

        List<String> jokes = plugin.getConfig().getStringList("jokes");
        jokes.add(newJoke);
        plugin.getConfig().set("jokes", jokes);
        plugin.saveConfig();

        player.sendMessage(ChatColor.GREEN + "Your joke has been added: " + newJoke);
        playSoundFromConfig(player, "sounds.joke_add");
    }

    private void playSoundFromConfig(Player player, String configPath) {
        String soundType = plugin.getConfig().getString(configPath + ".type", "ENTITY_VILLAGER_CELEBRATE");
        float volume = (float) plugin.getConfig().getDouble(configPath + ".volume", 1.0);
        float pitch = (float) plugin.getConfig().getDouble(configPath + ".pitch", 1.0);

        try {
            Sound sound = Sound.valueOf(soundType);
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid sound type in config: " + soundType);
            player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_0, 1.0f, 1.0f);
        }
    }


    private String getRandomJoke() {
        List<String> jokes = plugin.getConfig().getStringList("jokes");
        if (jokes.isEmpty()) {
            return null;
        }
        return jokes.get(new Random().nextInt(jokes.size()));
    }

    private boolean isOnCooldown(Player player, int cooldownSeconds) {
        UUID playerId = player.getUniqueId();
        if (!cooldowns.containsKey(playerId)) {
            return false;
        }
        long lastUsed = cooldowns.get(playerId);
        return (System.currentTimeMillis() - lastUsed) / 1000 < cooldownSeconds;
    }

    private long getTimeLeft(Player player, int cooldownSeconds) {
        UUID playerId = player.getUniqueId();
        long lastUsed = cooldowns.get(playerId);
        return cooldownSeconds - ((System.currentTimeMillis() - lastUsed) / 1000);
    }

    private void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
