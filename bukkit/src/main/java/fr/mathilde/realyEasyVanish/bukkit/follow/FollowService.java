package fr.mathilde.realyEasyVanish.bukkit.follow;

import fr.mathilde.realyEasyVanish.api.PlatformScheduler;
import fr.mathilde.realyEasyVanish.api.ReVanishPlayer;
import fr.mathilde.realyEasyVanish.bukkit.platform.BukkitVanishPlatform;
import fr.mathilde.realyEasyVanish.common.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Applies /vfollow and /vspec. Every mutation here touches one specific player, so it always
 * goes through PlatformScheduler#runForPlayer / #runRepeatingForPlayer, which on Folia dispatches
 * to that player's own region thread instead of the global one.
 */
public final class FollowService {

    private final VanishManager vanishManager;
    private final BukkitVanishPlatform platform;
    private final PlatformScheduler scheduler;
    private final Map<UUID, PlatformScheduler.SchedulerTask> followTasks = new ConcurrentHashMap<>();
    private final Map<UUID, GameMode> previousGameMode = new ConcurrentHashMap<>();

    public FollowService(VanishManager vanishManager, BukkitVanishPlatform platform) {
        this.vanishManager = vanishManager;
        this.platform = platform;
        this.scheduler = platform.scheduler();
    }

    public void onFollowChanged(Player follower, UUID targetUuid) {
        stopFollow(follower.getUniqueId());
        if (targetUuid == null) {
            return;
        }
        ReVanishPlayer followerRef = platform.wrap(follower);
        UUID followerUuid = follower.getUniqueId();
        PlatformScheduler.SchedulerTask task = scheduler.runRepeatingForPlayer(followerRef, () -> {
            Player self = Bukkit.getPlayer(followerUuid);
            Player target = Bukkit.getPlayer(targetUuid);
            if (self == null || target == null || !target.isOnline()) {
                stopFollow(followerUuid);
                return;
            }
            // teleportAsync (not teleport): a Folia region schedules its own tasks synchronously,
            // but the destination location may be owned by a different region's thread, which
            // plain teleport() cannot safely cross.
            self.teleportAsync(target.getLocation());
        }, vanishManager.config().followUpdateIntervalTicks());
        followTasks.put(followerUuid, task);
    }

    public void stopFollow(UUID followerUuid) {
        PlatformScheduler.SchedulerTask task = followTasks.remove(followerUuid);
        if (task != null) {
            task.cancel();
        }
    }

    public void onSpecChanged(Player specPlayer, UUID targetUuid) {
        ReVanishPlayer specRef = platform.wrap(specPlayer);
        UUID specUuid = specPlayer.getUniqueId();
        scheduler.runForPlayer(specRef, () -> {
            Player self = Bukkit.getPlayer(specUuid);
            if (self == null) {
                return;
            }
            if (targetUuid == null) {
                GameMode previous = previousGameMode.remove(specUuid);
                self.setGameMode(previous == null ? GameMode.SURVIVAL : previous);
                return;
            }
            Player target = Bukkit.getPlayer(targetUuid);
            if (target == null) {
                return;
            }
            previousGameMode.putIfAbsent(specUuid, self.getGameMode());
            self.setGameMode(GameMode.SPECTATOR);
            self.setSpectatorTarget(target);
        });
    }

    public void shutdown() {
        followTasks.values().forEach(PlatformScheduler.SchedulerTask::cancel);
        followTasks.clear();
    }
}
