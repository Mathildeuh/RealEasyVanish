package fr.mathilde.realyEasyVanish.api;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Transport used to keep vanish state consistent across backend servers and the proxy.
 * Implementations (plugin messaging today, Redis potentially later) are swappable without
 * VanishManager ever knowing which transport is in use.
 */
public interface SyncBridge {

    void publishVanishState(UUID player, boolean vanished, String sourceServer);

    void requestFullSync(String targetServer);

    void onStateReceived(Consumer<VanishSyncEvent> handler);
}
