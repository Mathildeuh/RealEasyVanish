package fr.mathilde.realyEasyVanish.bukkit.platform;

import fr.mathilde.realyEasyVanish.api.ReVanishPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public final class BukkitReVanishPlayer implements ReVanishPlayer {

    private final Plugin plugin;
    private final Player player;
    private final String serverName;

    public BukkitReVanishPlayer(Plugin plugin, Player player, String serverName) {
        this.plugin = plugin;
        this.player = player;
        this.serverName = serverName;
    }

    public Player handle() {
        return player;
    }

    @Override
    public UUID uuid() {
        return player.getUniqueId();
    }

    @Override
    public String name() {
        return player.getName();
    }

    @Override
    public void hideFrom(ReVanishPlayer viewer) {
        if (viewer instanceof BukkitReVanishPlayer bukkitViewer) {
            bukkitViewer.player.hidePlayer(plugin, player);
        }
    }

    @Override
    public void showTo(ReVanishPlayer viewer) {
        if (viewer instanceof BukkitReVanishPlayer bukkitViewer) {
            bukkitViewer.player.showPlayer(plugin, player);
        }
    }

    @Override
    public void sendMessage(Component message) {
        player.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }

    @Override
    public String currentServerName() {
        return serverName;
    }
}
