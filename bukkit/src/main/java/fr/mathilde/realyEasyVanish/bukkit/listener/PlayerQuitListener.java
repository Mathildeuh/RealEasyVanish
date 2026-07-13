package fr.mathilde.realyEasyVanish.bukkit.listener;

import fr.mathilde.realyEasyVanish.bukkit.follow.FollowService;
import fr.mathilde.realyEasyVanish.bukkit.scoreboard.VScoreboardService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class PlayerQuitListener implements Listener {

    private final FollowService followService;
    private final VScoreboardService scoreboardService;

    public PlayerQuitListener(FollowService followService, VScoreboardService scoreboardService) {
        this.followService = followService;
        this.scoreboardService = scoreboardService;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        followService.stopFollow(uuid);
        scoreboardService.onQuit(uuid);
    }
}
