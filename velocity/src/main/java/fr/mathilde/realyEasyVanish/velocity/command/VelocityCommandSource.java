package fr.mathilde.realyEasyVanish.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import fr.mathilde.realyEasyVanish.api.ReVanishCommandSource;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public final class VelocityCommandSource implements ReVanishCommandSource {

    private final CommandSource source;

    public VelocityCommandSource(CommandSource source) {
        this.source = source;
    }

    @Override
    public void sendMessage(Component message) {
        source.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return source.hasPermission(permission);
    }

    @Override
    public boolean isPlayer() {
        return source instanceof Player;
    }

    @Override
    public UUID playerUuid() {
        return source instanceof Player player ? player.getUniqueId() : null;
    }

    @Override
    public String name() {
        return source instanceof Player player ? player.getUsername() : "CONSOLE";
    }
}
