package fr.mathilde.realyEasyVanish.bukkit.scheduler;

import fr.mathilde.realyEasyVanish.api.PlatformScheduler;
import fr.mathilde.realyEasyVanish.api.ReVanishPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Fallback for plain Paper/Spigot (non-Folia): everything routes through the classic
 * single-threaded BukkitScheduler, which is safe there since there is only one server thread.
 */
public final class BukkitPlatformScheduler implements PlatformScheduler {

    private final Plugin plugin;

    public BukkitPlatformScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runGlobal(Runnable task) {
        Bukkit.getScheduler().runTask(plugin, task);
    }

    @Override
    public void runAsync(Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    @Override
    public void runForPlayer(ReVanishPlayer player, Runnable task) {
        Bukkit.getScheduler().runTask(plugin, task);
    }

    @Override
    public SchedulerTask runRepeatingGlobal(Runnable task, long periodTicks) {
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, task, periodTicks, periodTicks);
        return bukkitTask::cancel;
    }

    @Override
    public SchedulerTask runRepeatingForPlayer(ReVanishPlayer player, Runnable task, long periodTicks) {
        return runRepeatingGlobal(task, periodTicks);
    }
}
