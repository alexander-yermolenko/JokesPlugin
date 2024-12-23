package jokesPlugin.completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JokeCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("joke")) {
            List<String> completions = new ArrayList<>();
            if (args.length == 1) {
                // Provide suggestions for the first argument (subcommand)
                completions.add("add");
            } else if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
                // No suggestions for the joke text itself
                return Collections.emptyList();
            }
            return completions;
        }
        return null;
    }
}
