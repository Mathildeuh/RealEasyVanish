package fr.mathilde.realyEasyVanish.bukkit;

import fr.mathilde.realyEasyVanish.api.PlatformScheduler;
import fr.mathilde.realyEasyVanish.api.ReVanishCommandSource;
import fr.mathilde.realyEasyVanish.bukkit.command.BukkitCommandExecutor;
import fr.mathilde.realyEasyVanish.bukkit.config.BukkitConfigStore;
import fr.mathilde.realyEasyVanish.bukkit.follow.FollowService;
import fr.mathilde.realyEasyVanish.bukkit.listener.ChatListener;
import fr.mathilde.realyEasyVanish.bukkit.listener.ItemPickupListener;
import fr.mathilde.realyEasyVanish.bukkit.listener.PlayerJoinListener;
import fr.mathilde.realyEasyVanish.bukkit.listener.PlayerQuitListener;
import fr.mathilde.realyEasyVanish.bukkit.placeholder.ReVanishPlaceholderExpansion;
import fr.mathilde.realyEasyVanish.bukkit.platform.BukkitVanishPlatform;
import fr.mathilde.realyEasyVanish.bukkit.scheduler.BukkitPlatformScheduler;
import fr.mathilde.realyEasyVanish.bukkit.scheduler.FoliaPlatformScheduler;
import fr.mathilde.realyEasyVanish.bukkit.scheduler.FoliaSupport;
import fr.mathilde.realyEasyVanish.bukkit.scoreboard.VScoreboardService;
import fr.mathilde.realyEasyVanish.bukkit.sync.PluginMessageSyncBridge;
import fr.mathilde.realyEasyVanish.common.VanishManager;
import fr.mathilde.realyEasyVanish.common.command.IsVanishCommand;
import fr.mathilde.realyEasyVanish.common.command.ReVanishCommand;
import fr.mathilde.realyEasyVanish.common.command.VChatCommand;
import fr.mathilde.realyEasyVanish.common.command.VFollowCommand;
import fr.mathilde.realyEasyVanish.common.command.VListCommand;
import fr.mathilde.realyEasyVanish.common.command.VReloadCommand;
import fr.mathilde.realyEasyVanish.common.command.VScoreboardCommand;
import fr.mathilde.realyEasyVanish.common.command.VSpecCommand;
import fr.mathilde.realyEasyVanish.common.command.VanishCommand;
import fr.mathilde.realyEasyVanish.common.config.ConfigManager;
import fr.mathilde.realyEasyVanish.common.config.ReVanishConfig;
import fr.mathilde.realyEasyVanish.common.sync.SyncProtocol;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public final class ReVanishBukkitPlugin extends JavaPlugin {

    private VanishManager vanishManager;
    private ConfigManager configManager;
    private BukkitVanishPlatform platform;
    private PluginMessageSyncBridge syncBridge;
    private FollowService followService;
    private VScoreboardService scoreboardService;

    @Override
    public void onEnable() {
        PlatformScheduler scheduler = FoliaSupport.isFolia()
                ? new FoliaPlatformScheduler(this)
                : new BukkitPlatformScheduler(this);

        syncBridge = new PluginMessageSyncBridge(this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, SyncProtocol.CHANNEL);
        getServer().getMessenger().registerIncomingPluginChannel(this, SyncProtocol.CHANNEL, syncBridge);

        configManager = new ConfigManager(new BukkitConfigStore(this));
        ReVanishConfig config = configManager.load();

        platform = new BukkitVanishPlatform(this, config.serverName(), scheduler, syncBridge);
        vanishManager = new VanishManager(platform, config);

        followService = new FollowService(vanishManager, platform);
        scoreboardService = new VScoreboardService(vanishManager, platform);

        registerListeners();
        registerCommands();

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ReVanishPlaceholderExpansion(this, vanishManager).register();
        }

        getLogger().info(FoliaSupport.isFolia()
                ? "RealyEasyVanish enabled with the Folia region scheduler."
                : "RealyEasyVanish enabled with the classic Bukkit scheduler.");
    }

    @Override
    public void onDisable() {
        if (followService != null) {
            followService.shutdown();
        }
        if (scoreboardService != null) {
            scoreboardService.shutdown();
        }
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinListener(vanishManager, platform, scoreboardService), this);
        pm.registerEvents(new PlayerQuitListener(followService, scoreboardService), this);
        pm.registerEvents(new ChatListener(vanishManager), this);
        pm.registerEvents(new ItemPickupListener(vanishManager), this);
    }

    private void registerCommands() {
        List<ReVanishCommand> commands = List.of(
                new VanishCommand(vanishManager, platform),
                new VListCommand(vanishManager, platform),
                new VChatCommand(vanishManager),
                new VReloadCommand(vanishManager, configManager),
                new VFollowCommand(vanishManager, platform),
                new VSpecCommand(vanishManager, platform),
                new IsVanishCommand(vanishManager, platform),
                new VScoreboardCommand(vanishManager, platform)
        );
        for (ReVanishCommand command : commands) {
            PluginCommand pluginCommand = getCommand(command.name());
            if (pluginCommand == null) {
                getLogger().warning("Command not declared in plugin.yml: " + command.name());
                continue;
            }
            BukkitCommandExecutor executor = new BukkitCommandExecutor(command, this);
            pluginCommand.setExecutor(executor);
            pluginCommand.setTabCompleter(executor);
        }
    }

    /**
     * /vfollow, /vspec and /vscoreboard only store their target/toggle in VanishManager (which is
     * platform-agnostic); this is where that state gets turned into actual Bukkit-side effects.
     */
    public void onCommandExecuted(ReVanishCommand command, ReVanishCommandSource source) {
        if (!source.isPlayer()) {
            return;
        }
        UUID uuid = source.playerUuid();
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
        if (command instanceof VFollowCommand) {
            followService.onFollowChanged(player, vanishManager.stateOf(uuid).followTarget());
        } else if (command instanceof VSpecCommand) {
            followService.onSpecChanged(player, vanishManager.stateOf(uuid).specTarget());
        } else if (command instanceof VScoreboardCommand) {
            scoreboardService.setEnabled(player, vanishManager.scoreboardEnabled(uuid));
        }
    }
}
