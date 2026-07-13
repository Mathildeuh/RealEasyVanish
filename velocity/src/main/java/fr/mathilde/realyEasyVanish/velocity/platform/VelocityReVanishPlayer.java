package fr.mathilde.realyEasyVanish.velocity.platform;

import com.velocitypowered.api.proxy.Player;
import fr.mathilde.realyEasyVanish.api.ReVanishPlayer;
import net.kyori.adventure.text.Component;

import java.util.UUID;

/**
 * The proxy has no client rendering to control, so hideFrom/showTo are no-ops here; actual
 * visibility is enforced by each backend's BukkitReVanishPlayer once vanish state is synced.
 */
public final class VelocityReVanishPlayer implements ReVanishPlayer {

    private final Player player;

    public VelocityReVanishPlayer(Player player) {
        this.player = player;
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
        return player.getUsername();
    }

    @Override
    public void hideFrom(ReVanishPlayer viewer) {
    }

    @Override
    public void showTo(ReVanishPlayer viewer) {
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
        return player.getCurrentServer().map(sc -> sc.getServerInfo().getName()).orElse("unknown");
    }
}
