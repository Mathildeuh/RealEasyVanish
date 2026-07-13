package fr.mathilde.realyEasyVanish.bukkit.command;

import fr.mathilde.realyEasyVanish.common.VanishManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Shadows vanilla /list: Bukkit lets a plugin claim the same command label in plugin.yml, so this
 * takes priority over the built-in one and excludes vanished players for viewers without
 * revanish.see, instead of leaking them through the untouched vanilla command.
 */
public final class VanillaListCommand implements CommandExecutor {

    private final VanishManager vanishManager;

    public VanillaListCommand(VanishManager vanishManager) {
        this.vanishManager = vanishManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean seeVanished = sender.hasPermission("revanish.see");
        List<String> names = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (seeVanished || !vanishManager.isVanished(player.getUniqueId())) {
                names.add(player.getName());
            }
        }
        sender.sendMessage(Component.text(
                "There are " + names.size() + " of a max of " + Bukkit.getMaxPlayers() + " players online:",
                NamedTextColor.GRAY));
        sender.sendMessage(Component.text(String.join(", ", names), NamedTextColor.WHITE));
        return true;
    }
}
