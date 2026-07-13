package fr.mathilde.realyEasyVanish.api;

/**
 * Abstracts BukkitScheduler / Folia region schedulers / Velocity scheduler behind one API.
 * Any mutation tied to a specific entity (teleport, gamemode, packets) MUST go through
 * runForPlayer/runRepeatingForPlayer so the Folia implementation can dispatch it on the
 * correct region thread instead of the global one.
 */
public interface PlatformScheduler {

    void runGlobal(Runnable task);

    void runAsync(Runnable task);

    void runForPlayer(ReVanishPlayer player, Runnable task);

    SchedulerTask runDelayedGlobal(Runnable task, long delayTicks);

    SchedulerTask runRepeatingGlobal(Runnable task, long periodTicks);

    SchedulerTask runRepeatingForPlayer(ReVanishPlayer player, Runnable task, long periodTicks);

    interface SchedulerTask {
        void cancel();
    }
}
