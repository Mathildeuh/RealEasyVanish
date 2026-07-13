package fr.mathilde.realyEasyVanish.bukkit.ping;

import fr.mathilde.realyEasyVanish.common.VanishManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.Iterator;

/**
 * Fallback for plain Spigot (no Paper): the base event has no way to correct the displayed
 * online count, only the hover sample list, so that is the best available here.
 */
public final class BasicPingListener implements Listener {

    private final VanishManager vanishManager;

    public BasicPingListener(VanishManager vanishManager) {
        this.vanishManager = vanishManager;
    }

    @EventHandler
    @SuppressWarnings("removal") // iterator() is the only sample-editing API the base event has
    public void onPing(ServerListPingEvent event) {
        if (vanishManager.vanishedCount() == 0) {
            return;
        }
        try {
            Iterator<Player> iterator = event.iterator();
            while (iterator.hasNext()) {
                if (vanishManager.isVanished(iterator.next().getUniqueId())) {
                    iterator.remove();
                }
            }
        } catch (UnsupportedOperationException ignored) {
            // Nothing more we can do here without NMS access.
        }
    }
}
