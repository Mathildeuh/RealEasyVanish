package fr.mathilde.realyEasyVanish.bukkit.platform;

import fr.mathilde.realyEasyVanish.api.PlatformScheduler;
import fr.mathilde.realyEasyVanish.api.ReVanishPlayer;
import fr.mathilde.realyEasyVanish.api.SyncBridge;
import fr.mathilde.realyEasyVanish.api.VanishPlatform;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class BukkitVanishPlatform implements VanishPlatform {

    private final Plugin plugin;
    private final String serverName;
    private final PlatformScheduler scheduler;
    private final SyncBridge syncBridge;

    public BukkitVanishPlatform(Plugin plugin, String serverName, PlatformScheduler scheduler, SyncBridge syncBridge) {
        this.plugin = plugin;
        this.serverName = serverName;
        this.scheduler = scheduler;
        this.syncBridge = syncBridge;
    }

    @Override
    public Collection<ReVanishPlayer> onlinePlayers() {
        List<ReVanishPlayer> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            players.add(wrap(player));
        }
        return players;
    }

    @Override
    public Optional<ReVanishPlayer> player(UUID uuid) {
        return Optional.ofNullable(Bukkit.getPlayer(uuid)).map(this::wrap);
    }

    @Override
    public Optional<ReVanishPlayer> player(String name) {
        return Optional.ofNullable(Bukkit.getPlayerExact(name)).map(this::wrap);
    }

    @Override
    public void broadcast(Component message) {
        Bukkit.getServer().sendMessage(message);
    }

    @Override
    public PlatformScheduler scheduler() {
        return scheduler;
    }

    @Override
    public SyncBridge syncBridge() {
        return syncBridge;
    }

    @Override
    public boolean isProxy() {
        return false;
    }

    @Override
    public String serverName() {
        return serverName;
    }

    public BukkitReVanishPlayer wrap(Player player) {
        return new BukkitReVanishPlayer(plugin, player, serverName);
    }
}
