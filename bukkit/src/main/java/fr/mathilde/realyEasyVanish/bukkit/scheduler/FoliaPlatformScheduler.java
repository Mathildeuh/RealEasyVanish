package fr.mathilde.realyEasyVanish.bukkit.scheduler;

import fr.mathilde.realyEasyVanish.api.PlatformScheduler;
import fr.mathilde.realyEasyVanish.api.ReVanishPlayer;
import fr.mathilde.realyEasyVanish.bukkit.platform.BukkitReVanishPlayer;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

/**
 * Only ever instantiated when FoliaSupport#isFolia() is true (see ReVanishBukkitPlugin);
 * referencing io.papermc.paper.threadedregions.scheduler.* here is safe because this class
 * is never loaded on a plain Spigot server, which lacks those classes entirely.
 */
public final class FoliaPlatformScheduler implements PlatformScheduler {

    private final Plugin plugin;

    public FoliaPlatformScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runGlobal(Runnable task) {
        Bukkit.getGlobalRegionScheduler().run(plugin, ignored -> task.run());
    }

    @Override
    public void runAsync(Runnable task) {
        Bukkit.getAsyncScheduler().runNow(plugin, ignored -> task.run());
    }

    @Override
    public void runForPlayer(ReVanishPlayer player, Runnable task) {
        bukkitPlayer(player).ifPresent(p -> p.getScheduler().run(plugin, ignored -> task.run(), null));
    }

    @Override
    public SchedulerTask runDelayedGlobal(Runnable task, long delayTicks) {
        ScheduledTask scheduled = Bukkit.getGlobalRegionScheduler().runDelayed(plugin, ignored -> task.run(), delayTicks);
        return scheduled::cancel;
    }

    @Override
    public SchedulerTask runRepeatingGlobal(Runnable task, long periodTicks) {
        ScheduledTask scheduled = Bukkit.getGlobalRegionScheduler()
                .runAtFixedRate(plugin, ignored -> task.run(), periodTicks, periodTicks);
        return scheduled::cancel;
    }

    @Override
    public SchedulerTask runRepeatingForPlayer(ReVanishPlayer player, Runnable task, long periodTicks) {
        Optional<Player> target = bukkitPlayer(player);
        if (target.isEmpty()) {
            return () -> { };
        }
        ScheduledTask scheduled = target.get().getScheduler()
                .runAtFixedRate(plugin, ignored -> task.run(), null, periodTicks, periodTicks);
        return scheduled == null ? () -> { } : scheduled::cancel;
    }

    private Optional<Player> bukkitPlayer(ReVanishPlayer player) {
        if (player instanceof BukkitReVanishPlayer bukkitPlayer) {
            return Optional.ofNullable(bukkitPlayer.handle());
        }
        return Optional.ofNullable(Bukkit.getPlayer(player.uuid()));
    }
}
