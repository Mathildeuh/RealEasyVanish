package fr.mathilde.realyEasyVanish.velocity.diagnostics;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import fr.mathilde.realyEasyVanish.api.PlatformScheduler;
import fr.mathilde.realyEasyVanish.velocity.sync.VelocitySyncBridge;
import org.slf4j.Logger;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Periodically checks whether every backend that actually has players connected has ever spoken
 * on the sync channel; if not, RealyEasyVanish is very likely missing there, since it self-reports
 * (VANISH_UPDATE or, failing that, the one-off HELLO) as soon as it has a player to carry it.
 */
public final class BackendCoverageService {

    private final ProxyServer proxyServer;
    private final Logger logger;
    private final Set<String> confirmedServers = ConcurrentHashMap.newKeySet();
    private final Set<String> warnedServers = ConcurrentHashMap.newKeySet();

    public BackendCoverageService(ProxyServer proxyServer, PlatformScheduler scheduler, Logger logger,
                                   VelocitySyncBridge syncBridge) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        syncBridge.onServerConfirmed(confirmedServers::add);
        scheduler.runRepeatingGlobal(this::sweep, 20L * 60);
    }

    private void sweep() {
        for (RegisteredServer server : proxyServer.getAllServers()) {
            String name = server.getServerInfo().getName();
            if (confirmedServers.contains(name) || server.getPlayersConnected().isEmpty()) {
                continue;
            }
            if (warnedServers.add(name)) {
                logger.warn("Backend server '{}' has players connected but has never responded on the "
                        + "RealyEasyVanish sync channel. Install RealyEasyVanish on that server too so "
                        + "vanish state stays in sync across your network.", name);
            }
        }
    }
}
