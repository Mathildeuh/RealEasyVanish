package fr.mathilde.realyEasyVanish.bukkit.ping;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import fr.mathilde.realyEasyVanish.common.VanishManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Only ever instantiated when PaperPingSupport#isAvailable() is true; this Paper-only event class
 * is never loaded on plain Spigot, which lacks it entirely. Unlike the base ServerListPingEvent,
 * this one allows correcting the displayed online count, not just the hover sample - and its
 * getListedPlayers() (unlike the deprecated iterator()/getPlayerSample()) is the current,
 * non-deprecated way to edit that sample.
 */
public final class PaperPingListener implements Listener {

    private final VanishManager vanishManager;

    public PaperPingListener(VanishManager vanishManager) {
        this.vanishManager = vanishManager;
    }

    @EventHandler
    public void onPing(PaperServerListPingEvent event) {
        int vanishedCount = vanishManager.vanishedCount();
        if (vanishedCount == 0) {
            return;
        }
        event.setNumPlayers(Math.max(0, event.getNumPlayers() - vanishedCount));
        event.getListedPlayers().removeIf(listed -> vanishManager.isVanished(listed.id()));
    }
}
