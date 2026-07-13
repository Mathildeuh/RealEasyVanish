package fr.mathilde.realyEasyVanish.velocity.platform;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.mathilde.realyEasyVanish.api.PlatformScheduler;
import fr.mathilde.realyEasyVanish.api.ReVanishPlayer;
import fr.mathilde.realyEasyVanish.api.SyncBridge;
import fr.mathilde.realyEasyVanish.api.VanishPlatform;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class VelocityVanishPlatform implements VanishPlatform {

    private final ProxyServer proxyServer;
    private final PlatformScheduler scheduler;
    private final SyncBridge syncBridge;

    public VelocityVanishPlatform(ProxyServer proxyServer, PlatformScheduler scheduler, SyncBridge syncBridge) {
        this.proxyServer = proxyServer;
        this.scheduler = scheduler;
        this.syncBridge = syncBridge;
    }

    @Override
    public Collection<ReVanishPlayer> onlinePlayers() {
        List<ReVanishPlayer> players = new ArrayList<>();
        for (Player player : proxyServer.getAllPlayers()) {
            players.add(wrap(player));
        }
        return players;
    }

    @Override
    public Optional<ReVanishPlayer> player(UUID uuid) {
        return proxyServer.getPlayer(uuid).map(this::wrap);
    }

    @Override
    public Optional<ReVanishPlayer> player(String name) {
        return proxyServer.getPlayer(name).map(this::wrap);
    }

    @Override
    public void broadcast(Component message) {
        proxyServer.sendMessage(message);
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
        return true;
    }

    @Override
    public String serverName() {
        return "proxy";
    }

    public VelocityReVanishPlayer wrap(Player player) {
        return new VelocityReVanishPlayer(player);
    }
}
