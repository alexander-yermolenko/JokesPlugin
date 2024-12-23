package jokesPlugin.commands;

import jokesPlugin.JokesPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class JokeCommand implements CommandExecutor {

    private final JokesPlugin plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>(); // Store player cooldowns

    public JokeCommand(JokesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("joke")) {
            if (sender instanceof Player player) {

                // Get cooldown time from the config
                int cooldownSeconds = plugin.getConfig().getInt("cooldown-seconds");

                // Cooldown seconds isn't set up
                if (plugin.getConfig().get("cooldown-seconds") == null) {
                    cooldownSeconds = 20; // Default cooldown seconds: 20
                }

                // Check cooldown
                if (isOnCooldown(player, cooldownSeconds)) {
                    long timeLeft = getTimeLeft(player, cooldownSeconds);
                    player.sendMessage(ChatColor.YELLOW + "You must wait " + timeLeft + " seconds before using this command again.");
                    return true;
                }

                // Get a random joke
                String joke = getRandomJoke();
                if (joke != null) {
                    player.sendMessage(ChatColor.GREEN + "Here's a joke for you: " + joke);
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 1.0f, 1.0f);
                    setCooldown(player); // Set cooldown after command execution
                } else {
                    player.sendMessage(ChatColor.RED +"No jokes found in the configuration!");
                }
            } else {
                plugin.getLogger().info(ChatColor.RED + "Only players can use the /joke command.");
            }
            return true;
        }
        return false;
    }

    private String getRandomJoke() {
        List<String> jokes = plugin.getConfig().getStringList("jokes");
        if (jokes.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return jokes.get(random.nextInt(jokes.size()));
    }

    private boolean isOnCooldown(Player player, int cooldownSeconds) {
        UUID playerId = player.getUniqueId();
        if (!cooldowns.containsKey(playerId)) {
            return false; // Player is not on cooldown
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
