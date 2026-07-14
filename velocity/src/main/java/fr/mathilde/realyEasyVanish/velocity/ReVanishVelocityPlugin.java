package fr.mathilde.realyEasyVanish.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.mathilde.realyEasyVanish.api.PlatformScheduler;
import fr.mathilde.realyEasyVanish.api.RealyEasyVanishAPI;
import fr.mathilde.realyEasyVanish.common.VanishManager;
import fr.mathilde.realyEasyVanish.common.command.IsVanishCommand;
import fr.mathilde.realyEasyVanish.common.command.ReVanishCommand;
import fr.mathilde.realyEasyVanish.common.command.VListCommand;
import fr.mathilde.realyEasyVanish.common.command.VReloadCommand;
import fr.mathilde.realyEasyVanish.common.command.VanishCommand;
import fr.mathilde.realyEasyVanish.common.config.ConfigManager;
import fr.mathilde.realyEasyVanish.common.config.ReVanishConfig;
import fr.mathilde.realyEasyVanish.common.sync.SyncProtocol;
import fr.mathilde.realyEasyVanish.velocity.command.VelocitySimpleCommand;
import fr.mathilde.realyEasyVanish.velocity.config.VelocityConfigStore;
import fr.mathilde.realyEasyVanish.velocity.diagnostics.BackendCoverageService;
import fr.mathilde.realyEasyVanish.velocity.ping.ProxyPingListener;
import fr.mathilde.realyEasyVanish.velocity.platform.VelocityVanishPlatform;
import fr.mathilde.realyEasyVanish.velocity.scheduler.VelocityPlatformScheduler;
import fr.mathilde.realyEasyVanish.velocity.sync.VelocitySyncBridge;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Plugin(id = "revanish", name = "RealyEasyVanish", version = "1.0.0", authors = {"Mathildeuh"})
public final class ReVanishVelocityPlugin {

    private final ProxyServer proxyServer;
    private final Logger logger;
    private final Path dataDirectory;

    private VanishManager vanishManager;
    private VelocitySyncBridge syncBridge;

    @Inject
    public ReVanishVelocityPlugin(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        PlatformScheduler scheduler = new VelocityPlatformScheduler(this, proxyServer);
        syncBridge = new VelocitySyncBridge(proxyServer);
        proxyServer.getEventManager().register(this, syncBridge);

        ConfigManager configManager = new ConfigManager(new VelocityConfigStore(dataDirectory));
        ReVanishConfig config = configManager.load();

        VelocityVanishPlatform platform = new VelocityVanishPlatform(proxyServer, scheduler, syncBridge);
        vanishManager = new VanishManager(platform, config);
        RealyEasyVanishAPI.register(vanishManager);

        registerCommands(platform, configManager);
        proxyServer.getEventManager().register(this, new ProxyPingListener(vanishManager));
        new BackendCoverageService(proxyServer, scheduler, logger, syncBridge);
    }

    /**
     * Only commands with real network-wide meaning are registered here. /vchat, /vfollow, /vspec
     * and /vscoreboard only matter on whichever backend the player is actually on: if we also
     * registered them here, Velocity would intercept them before they ever reach that backend
     * (proxy-registered commands always take priority), permanently breaking them for anyone
     * connected through the proxy instead of just leaving them without proxy-level behavior.
     */
    private void registerCommands(VelocityVanishPlatform platform, ConfigManager configManager) {
        List<ReVanishCommand> commands = List.of(
                new VanishCommand(vanishManager, platform),
                new VListCommand(vanishManager, platform),
                new VReloadCommand(vanishManager, configManager),
                new IsVanishCommand(vanishManager, platform)
        );
        CommandManager commandManager = proxyServer.getCommandManager();
        for (ReVanishCommand command : commands) {
            CommandMeta meta = commandManager.metaBuilder(command.name())
                    .aliases(command.aliases().toArray(new String[0]))
                    .plugin(this)
                    .build();
            commandManager.register(meta, new VelocitySimpleCommand(command));
        }
    }

    /**
     * Pushes the currently known vanish set to a backend the moment a player finishes connecting
     * to it, so that server never has a window where a vanished player looks visible before the
     * next VANISH_UPDATE happens to be sent.
     */
    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        Map<UUID, Boolean> snapshot = new HashMap<>();
        for (UUID uuid : vanishManager.vanishedUuids()) {
            snapshot.put(uuid, true);
        }
        event.getServer().sendPluginMessage(VelocitySyncBridge.CHANNEL,
                SyncProtocol.encodeFullSyncResponse(snapshot, "proxy"));
    }
}
