package fr.mathilde.realyEasyVanish.api;

import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface VanishPlatform {

    Collection<ReVanishPlayer> onlinePlayers();

    Optional<ReVanishPlayer> player(UUID uuid);

    Optional<ReVanishPlayer> player(String name);

    void broadcast(Component message);

    PlatformScheduler scheduler();

    SyncBridge syncBridge();

    boolean isProxy();

    String serverName();
}
