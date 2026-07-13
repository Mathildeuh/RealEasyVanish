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
 *
 * Reuses whatever scoreboard the player already has (from another plugin, e.g. one that manages
 * nametag teams) instead of replacing it outright, and on disable only removes our own objective -
 * never resets the player back to the server's shared main scoreboard, which would permanently
 * destroy anything another plugin had set up for them.
 */
public final class VScoreboardService {

    private static final String OBJECTIVE_NAME = "revanish";

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
        UUID uuid = player.getUniqueId();
        stop(uuid);
        if (!enabled) {
            Scoreboard board = player.getScoreboard();
            Objective objective = board.getObjective(OBJECTIVE_NAME);
            if (objective != null) {
                objective.unregister();
            }
            return;
        }

        Scoreboard board = player.getScoreboard();
        if (board == null || board.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            board = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(board);
        }
        Objective objective = board.getObjective(OBJECTIVE_NAME);
        if (objective == null) {
            objective = board.registerNewObjective(OBJECTIVE_NAME, Criteria.DUMMY, "Vanished players");
        }
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        ReVanishPlayer ref = platform.wrap(player);
        Objective finalObjective = objective;
        PlatformScheduler.SchedulerTask task = scheduler.runRepeatingForPlayer(ref,
                () -> update(uuid, finalObjective), vanishManager.config().scoreboardUpdateIntervalTicks());
        tasks.put(uuid, task);
        update(uuid, objective);
    }

    private void update(UUID uuid, Objective objective) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline() || !objective.getScoreboard().equals(player.getScoreboard())) {
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
