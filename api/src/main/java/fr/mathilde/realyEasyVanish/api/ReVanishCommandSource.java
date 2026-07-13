package fr.mathilde.realyEasyVanish.api;

import net.kyori.adventure.text.Component;

import java.util.UUID;

/**
 * Wraps a Bukkit CommandSender or a Velocity CommandSource so command logic in :common
 * stays platform-agnostic.
 */
public interface ReVanishCommandSource {

    void sendMessage(Component message);

    boolean hasPermission(String permission);

    boolean isPlayer();

    UUID playerUuid();

    String name();
}
