package fr.mathilde.realyEasyVanish.bukkit.listener;

import fr.mathilde.realyEasyVanish.common.VanishManager;
import fr.mathilde.realyEasyVanish.common.VanishState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public final class ItemPickupListener implements Listener {

    private final VanishManager vanishManager;

    public ItemPickupListener(VanishManager vanishManager) {
        this.vanishManager = vanishManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        VanishState state = vanishManager.stateOf(player.getUniqueId());
        if (state.vanished() && !state.pickupItems()) {
            event.setCancelled(true);
        }
    }
}
