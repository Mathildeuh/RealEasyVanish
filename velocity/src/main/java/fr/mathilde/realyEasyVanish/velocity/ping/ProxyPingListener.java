package fr.mathilde.realyEasyVanish.velocity.ping;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import fr.mathilde.realyEasyVanish.common.VanishManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Corrects the aggregate network-wide ping shown in the multiplayer server list: Velocity answers
 * pings itself (it does not simply forward a backend's response), so this is what most players
 * actually see, unlike each individual backend's own ping.
 */
public final class ProxyPingListener {

    private final VanishManager vanishManager;

    public ProxyPingListener(VanishManager vanishManager) {
        this.vanishManager = vanishManager;
    }

    @Subscribe
    public void onPing(ProxyPingEvent event) {
        Set<java.util.UUID> vanished = Set.copyOf(vanishManager.vanishedUuids());
        if (vanished.isEmpty()) {
            return;
        }
        ServerPing ping = event.getPing();
        int correctedOnline = Math.max(0, ping.getPlayers().map(ServerPing.Players::getOnline).orElse(0) - vanished.size());

        List<ServerPing.SamplePlayer> sample = new ArrayList<>();
        ping.getPlayers().ifPresent(players -> {
            for (ServerPing.SamplePlayer samplePlayer : players.getSample()) {
                if (!vanished.contains(samplePlayer.getId())) {
                    sample.add(samplePlayer);
                }
            }
        });

        ServerPing corrected = ping.asBuilder()
                .onlinePlayers(correctedOnline)
                .samplePlayers(sample.toArray(new ServerPing.SamplePlayer[0]))
                .build();
        event.setPing(corrected);
    }
}
