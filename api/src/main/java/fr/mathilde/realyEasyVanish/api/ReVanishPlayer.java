package fr.mathilde.realyEasyVanish.api;

import net.kyori.adventure.text.Component;

import java.util.UUID;

public interface ReVanishPlayer {

    UUID uuid();

    String name();

    /**
     * No-op on platforms with no client rendering to control (e.g. Velocity).
     */
    void hideFrom(ReVanishPlayer viewer);

    void showTo(ReVanishPlayer viewer);

    void sendMessage(Component message);

    boolean hasPermission(String permission);

    String currentServerName();
}
