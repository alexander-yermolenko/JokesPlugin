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

                // Default cooldown time isn't set up
                if (plugin.getConfig().get("cooldown-seconds") == null) {
                    cooldownSeconds = 30; // Default cooldown time is 20
                }

                // Subcommand logic for "/joke add"
                if (args.length > 0 && args[0].equalsIgnoreCase("add")) {
                    // Combine all the arguments after "add" into a single string (the joke)
                    StringBuilder jokeBuilder = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        jokeBuilder.append(args[i]).append(" ");
                    }
                    String joke = jokeBuilder.toString().trim();

                    if (args.length < 2) {
                        player.sendMessage(ChatColor.RED + "Usage: /joke add <your joke>");
                        return true;
                    }



                    // Check cooldown for adding jokes
                    if (isOnCooldown(player, cooldownSeconds)) {
                        long timeLeft = getTimeLeft(player, cooldownSeconds);
                        player.sendMessage(ChatColor.YELLOW + "You must wait " + timeLeft + " seconds before adding another joke.");
                        return true;
                    }

                    // Combine the rest of the arguments into the joke text
                    String newJoke = String.join(" ", args).substring(4);

                    // Check joke length limit if you want to enforce it
                    int maxLength = plugin.getConfig().getInt("max-joke-length");

                    // Default joke length isn't set up
                    if (plugin.getConfig().get("max-joke-length") == null) {
                        maxLength = 150; // Default joke length is 150
                    }

                    if (newJoke.length() > maxLength) {
                        player.sendMessage(ChatColor.RED + "Your joke is too long! Maximum length is " + maxLength + " characters.");
                        return true;
                    }

                    // Save the joke to the config
                    List<String> jokes = plugin.getConfig().getStringList("jokes");
                    jokes.add(newJoke);
                    plugin.getConfig().set("jokes", jokes);
                    plugin.saveConfig();

                    player.sendMessage(ChatColor.GREEN + "Your joke has been added: " + newJoke);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

                    setCooldown(player); // Set cooldown for adding jokes
                    return true;
                }

                // Default: tell a joke
                // Check cooldown for /joke command
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
                    setCooldown(player); // Set cooldown for telling jokes
                } else {
                    player.sendMessage(ChatColor.RED + "No jokes found in the configuration!");
                }

            } else {
                plugin.getLogger().info(ChatColor.RED + "Only players can use this command.");
            }
            return true;
        }
        return false;
    }

    private String getRandomJoke() {
        List<String> jokes = plugin.getConfig().getStringList("jokes");
        if (jokes.isEmpty()) {
            return null; // Return null if no jokes are found
        }
        Random random = new Random();
        return jokes.get(random.nextInt(jokes.size())); // Randomly pick a joke
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
