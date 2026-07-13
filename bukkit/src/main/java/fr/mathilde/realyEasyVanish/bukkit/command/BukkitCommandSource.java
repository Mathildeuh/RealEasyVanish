package fr.mathilde.realyEasyVanish.bukkit.command;

import fr.mathilde.realyEasyVanish.api.ReVanishCommandSource;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class BukkitCommandSource implements ReVanishCommandSource {

    private final CommandSender sender;

    public BukkitCommandSource(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(Component message) {
        sender.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean isPlayer() {
        return sender instanceof Player;
    }

    @Override
    public UUID playerUuid() {
        return sender instanceof Player player ? player.getUniqueId() : null;
    }

    @Override
    public String name() {
        return sender.getName();
    }
}
