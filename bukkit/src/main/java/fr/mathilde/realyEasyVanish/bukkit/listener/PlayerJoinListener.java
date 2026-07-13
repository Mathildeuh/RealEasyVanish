package fr.mathilde.realyEasyVanish.bukkit.listener;

import fr.mathilde.realyEasyVanish.bukkit.platform.BukkitVanishPlatform;
import fr.mathilde.realyEasyVanish.bukkit.scoreboard.VScoreboardService;
import fr.mathilde.realyEasyVanish.bukkit.sync.PluginMessageSyncBridge;
import fr.mathilde.realyEasyVanish.common.VanishManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerJoinListener implements Listener {

    private final VanishManager vanishManager;
    private final BukkitVanishPlatform platform;
    private final VScoreboardService scoreboardService;
    private final PluginMessageSyncBridge syncBridge;

    public PlayerJoinListener(VanishManager vanishManager, BukkitVanishPlatform platform,
                               VScoreboardService scoreboardService, PluginMessageSyncBridge syncBridge) {
        this.vanishManager = vanishManager;
        this.platform = platform;
        this.scoreboardService = scoreboardService;
        this.syncBridge = syncBridge;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        vanishManager.applyVisibilityToJoiner(platform.wrap(player));
        scoreboardService.onJoin(player);
        syncBridge.announcePresenceOnce(vanishManager.config().serverName());
    }
}
