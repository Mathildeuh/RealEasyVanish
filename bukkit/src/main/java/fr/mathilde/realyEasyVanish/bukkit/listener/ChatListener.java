package fr.mathilde.realyEasyVanish.bukkit.listener;

import fr.mathilde.realyEasyVanish.common.VanishManager;
import fr.mathilde.realyEasyVanish.common.VanishState;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Uses the legacy AsyncPlayerChatEvent (not Paper's AsyncChatEvent) on purpose: it is present on
 * both Spigot and Paper, whereas the Paper-only event would throw NoSuchMethodError/verification
 * errors on a pure Spigot runtime since this module only compiles against paper-api.
 */
public final class ChatListener implements Listener {

    private final VanishManager vanishManager;

    public ChatListener(VanishManager vanishManager) {
        this.vanishManager = vanishManager;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        VanishState state = vanishManager.stateOf(player.getUniqueId());
        if (state.vanished() && state.chatBlocked() && vanishManager.config().chatConfirmEnabled()) {
            event.setCancelled(true);
            player.sendMessage(Component.text(
                    "You are vanished, your message was not sent. Use /vchat to change this.",
                    NamedTextColor.RED));
        }
    }
}
