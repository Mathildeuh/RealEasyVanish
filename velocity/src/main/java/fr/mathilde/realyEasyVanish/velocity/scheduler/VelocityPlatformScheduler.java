package fr.mathilde.realyEasyVanish.velocity.scheduler;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import fr.mathilde.realyEasyVanish.api.PlatformScheduler;
import fr.mathilde.realyEasyVanish.api.ReVanishPlayer;

import java.util.concurrent.TimeUnit;

/**
 * The proxy has no per-region concept (that is purely a Folia/backend-server thing), so
 * runForPlayer just runs on Velocity's own scheduler like everything else here. Ticks are
 * approximated as 50ms to keep the same periodTicks unit used by common/config.
 */
public final class VelocityPlatformScheduler implements PlatformScheduler {

    private final Object pluginInstance;
    private final ProxyServer proxyServer;

    public VelocityPlatformScheduler(Object pluginInstance, ProxyServer proxyServer) {
        this.pluginInstance = pluginInstance;
        this.proxyServer = proxyServer;
    }

    @Override
    public void runGlobal(Runnable task) {
        proxyServer.getScheduler().buildTask(pluginInstance, task).schedule();
    }

    @Override
    public void runAsync(Runnable task) {
        proxyServer.getScheduler().buildTask(pluginInstance, task).schedule();
    }

    @Override
    public void runForPlayer(ReVanishPlayer player, Runnable task) {
        runGlobal(task);
    }

    @Override
    public SchedulerTask runRepeatingGlobal(Runnable task, long periodTicks) {
        ScheduledTask scheduled = proxyServer.getScheduler().buildTask(pluginInstance, task)
                .repeat(periodTicks * 50L, TimeUnit.MILLISECONDS)
                .schedule();
        return scheduled::cancel;
    }

    @Override
    public SchedulerTask runRepeatingForPlayer(ReVanishPlayer player, Runnable task, long periodTicks) {
        return runRepeatingGlobal(task, periodTicks);
    }
}
