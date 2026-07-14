package fr.mathilde.realyEasyVanish.bukkit.command;

import fr.mathilde.realyEasyVanish.api.ReVanishCommandSource;
import fr.mathilde.realyEasyVanish.bukkit.ReVanishBukkitPlugin;
import fr.mathilde.realyEasyVanish.common.command.ReVanishCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class BukkitCommandExecutor implements CommandExecutor, TabCompleter {

    private final ReVanishCommand command;
    private final ReVanishBukkitPlugin plugin;

    public BukkitCommandExecutor(ReVanishCommand command, ReVanishBukkitPlugin plugin) {
        this.command = command;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command bukkitCommand, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(command.permission())) {
            sender.sendMessage(Component.text("You do not have permission to use this command.", NamedTextColor.RED));
            return true;
        }
        ReVanishCommandSource source = new BukkitCommandSource(sender);
        command.execute(source, args);
        plugin.onCommandExecuted(command, source);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command bukkitCommand, @NotNull String alias, @NotNull String[] args) {
        return command.tabComplete(new BukkitCommandSource(sender), args);
    }
}
