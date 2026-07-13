package fr.mathilde.realyEasyVanish.bukkit.scoreboard;

import fr.mathilde.realyEasyVanish.api.PlatformScheduler;
import fr.mathilde.realyEasyVanish.api.ReVanishPlayer;
import fr.mathilde.realyEasyVanish.bukkit.platform.BukkitVanishPlatform;
import fr.mathilde.realyEasyVanish.common.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-player sidebar, so updates run through PlatformScheduler#runRepeatingForPlayer (required on
 * Folia since sending packets tied to a specific player must happen on that player's region).
 */
public final class VScoreboardService {

    private final VanishManager vanishManager;
    private final BukkitVanishPlatform platform;
    private final PlatformScheduler scheduler;
    private final Map<UUID, PlatformScheduler.SchedulerTask> tasks = new ConcurrentHashMap<>();

    public VScoreboardService(VanishManager vanishManager, BukkitVanishPlatform platform) {
        this.vanishManager = vanishManager;
        this.platform = platform;
        this.scheduler = platform.scheduler();
    }

    public void setEnabled(Player player, boolean enabled) {
        stop(player.getUniqueId());
        if (!enabled) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            return;
        }
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("revanish", Criteria.DUMMY, "Vanished players");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(board);

        ReVanishPlayer ref = platform.wrap(player);
        UUID uuid = player.getUniqueId();
        PlatformScheduler.SchedulerTask task = scheduler.runRepeatingForPlayer(ref,
                () -> update(uuid, objective), vanishManager.config().scoreboardUpdateIntervalTicks());
        tasks.put(uuid, task);
        update(uuid, objective);
    }

    private void update(UUID uuid, Objective objective) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline()) {
            return;
        }
        Scoreboard board = objective.getScoreboard();
        if (board == null) {
            return;
        }
        for (String entry : Set.copyOf(board.getEntries())) {
            board.resetScores(entry);
        }
        List<String> names = vanishManager.vanishedNames();
        int score = names.size();
        for (String name : names) {
            objective.getScore(name).setScore(score--);
        }
    }

    public void onJoin(Player player) {
        if (vanishManager.scoreboardEnabled(player.getUniqueId())) {
            setEnabled(player, true);
        }
    }

    public void onQuit(UUID uuid) {
        stop(uuid);
    }

    private void stop(UUID uuid) {
        PlatformScheduler.SchedulerTask task = tasks.remove(uuid);
        if (task != null) {
            task.cancel();
        }
    }

    public void shutdown() {
        tasks.values().forEach(PlatformScheduler.SchedulerTask::cancel);
        tasks.clear();
    }
}
