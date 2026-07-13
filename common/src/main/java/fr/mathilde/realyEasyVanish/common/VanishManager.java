package fr.mathilde.realyEasyVanish.common;

import fr.mathilde.realyEasyVanish.api.PlaceholderSource;
import fr.mathilde.realyEasyVanish.api.ReVanishPlayer;
import fr.mathilde.realyEasyVanish.api.VanishPlatform;
import fr.mathilde.realyEasyVanish.common.config.ReVanishConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Local source of truth for vanish state on this instance (a backend server, or the proxy).
 * Every mutation that touches a specific online player goes through PlatformScheduler#runForPlayer
 * so the Folia implementation can dispatch it on the correct region thread; nothing here blocks.
 */
public final class VanishManager implements PlaceholderSource {

    private final VanishPlatform platform;
    private final Map<UUID, VanishState> states = new ConcurrentHashMap<>();
    private final Set<UUID> scoreboardEnabled = ConcurrentHashMap.newKeySet();
    private volatile ReVanishConfig config;

    public VanishManager(VanishPlatform platform, ReVanishConfig config) {
        this.platform = platform;
        this.config = config;
        platform.syncBridge().onStateReceived(event -> applyRemoteState(event.player(), event.vanished()));
    }

    public void reload(ReVanishConfig config) {
        this.config = config;
    }

    public ReVanishConfig config() {
        return config;
    }

    public VanishPlatform platform() {
        return platform;
    }

    public VanishState stateOf(UUID uuid) {
        return states.getOrDefault(uuid, VanishState.defaults());
    }

    @Override
    public boolean isVanished(UUID uuid) {
        return stateOf(uuid).vanished();
    }

    public boolean toggle(UUID uuid) {
        boolean next = !isVanished(uuid);
        setVanished(uuid, next);
        return next;
    }

    public void setVanished(UUID uuid, boolean vanished) {
        states.compute(uuid, (id, current) -> orDefault(current).withVanished(vanished));
        applyVisibility(uuid, vanished);
        platform.syncBridge().publishVanishState(uuid, vanished, platform.serverName());
    }

    private void applyRemoteState(UUID uuid, boolean vanished) {
        states.compute(uuid, (id, current) -> orDefault(current).withVanished(vanished));
        applyVisibility(uuid, vanished);
    }

    private void applyVisibility(UUID uuid, boolean vanished) {
        platform.player(uuid).ifPresent(target -> {
            for (ReVanishPlayer viewer : platform.onlinePlayers()) {
                if (viewer.uuid().equals(uuid)) {
                    continue;
                }
                platform.scheduler().runForPlayer(viewer, () -> {
                    if (vanished && !viewer.hasPermission("revanish.see")) {
                        target.hideFrom(viewer);
                    } else {
                        target.showTo(viewer);
                    }
                });
            }
        });
    }

    public void applyVisibilityToJoiner(ReVanishPlayer joiner) {
        if (joiner.hasPermission("revanish.see")) {
            return;
        }
        for (Map.Entry<UUID, VanishState> entry : states.entrySet()) {
            if (!entry.getValue().vanished()) {
                continue;
            }
            platform.player(entry.getKey()).ifPresent(vanishedPlayer ->
                    platform.scheduler().runForPlayer(joiner, () -> vanishedPlayer.hideFrom(joiner)));
        }
    }

    public void setPickupItems(UUID uuid, boolean pickupItems) {
        states.compute(uuid, (id, current) -> orDefault(current).withPickupItems(pickupItems));
    }

    public void setChatBlocked(UUID uuid, boolean chatBlocked) {
        states.compute(uuid, (id, current) -> orDefault(current).withChatBlocked(chatBlocked));
    }

    public void setFollowTarget(UUID uuid, UUID target) {
        states.compute(uuid, (id, current) -> orDefault(current).withFollowTarget(target));
    }

    public void setSpecTarget(UUID uuid, UUID target) {
        states.compute(uuid, (id, current) -> orDefault(current).withSpecTarget(target));
    }

    public boolean toggleScoreboard(UUID uuid) {
        if (!scoreboardEnabled.remove(uuid)) {
            scoreboardEnabled.add(uuid);
            return true;
        }
        return false;
    }

    public boolean scoreboardEnabled(UUID uuid) {
        return scoreboardEnabled.contains(uuid);
    }

    public Collection<UUID> vanishedUuids() {
        List<UUID> result = new ArrayList<>();
        for (Map.Entry<UUID, VanishState> entry : states.entrySet()) {
            if (entry.getValue().vanished()) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    private static VanishState orDefault(VanishState state) {
        return state == null ? VanishState.defaults() : state;
    }

    @Override
    public boolean pickupEnabled(UUID uuid) {
        return stateOf(uuid).pickupItems();
    }

    @Override
    public int vanishedCount() {
        return vanishedUuids().size();
    }

    @Override
    public int visibleOnlineCount() {
        return platform.onlinePlayers().size() - vanishedCount();
    }

    @Override
    public String prefix() {
        return config.vanishPrefix();
    }

    @Override
    public List<String> vanishedNames() {
        List<String> names = new ArrayList<>();
        for (UUID uuid : vanishedUuids()) {
            platform.player(uuid).ifPresent(p -> names.add(p.name()));
        }
        return names;
    }

    @Override
    public List<String> visiblePlayerNames() {
        List<String> names = new ArrayList<>();
        for (ReVanishPlayer player : platform.onlinePlayers()) {
            if (!isVanished(player.uuid())) {
                names.add(player.name());
            }
        }
        return names;
    }
}
