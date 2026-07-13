package fr.mathilde.realyEasyVanish.bukkit.sync;

import fr.mathilde.realyEasyVanish.api.SyncBridge;
import fr.mathilde.realyEasyVanish.api.VanishSyncEvent;
import fr.mathilde.realyEasyVanish.common.sync.SyncProtocol;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Carries sync packets over a connected player's connection (plugin messaging has no meaning
 * without one). A backend with zero online players has nobody to render vanish state for anyway,
 * so this is not a real limitation in practice.
 */
public final class PluginMessageSyncBridge implements SyncBridge, PluginMessageListener {

    private final Plugin plugin;
    private final List<Consumer<VanishSyncEvent>> handlers = new CopyOnWriteArrayList<>();
    private volatile boolean receivedAnything;
    private volatile boolean helloSent;

    public PluginMessageSyncBridge(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void publishVanishState(UUID player, boolean vanished, String sourceServer) {
        send(SyncProtocol.encodeVanishUpdate(player, vanished, sourceServer));
    }

    @Override
    public void requestFullSync(String targetServer) {
        send(SyncProtocol.encodeFullSyncRequest(targetServer));
    }

    @Override
    public void onStateReceived(Consumer<VanishSyncEvent> handler) {
        handlers.add(handler);
    }

    /**
     * Fired once, opportunistically, the first time a player is online to carry it: lets the
     * proxy confirm this backend runs RealyEasyVanish even if nobody has ever vanished yet.
     */
    public void announcePresenceOnce(String serverName) {
        if (helloSent) {
            return;
        }
        helloSent = true;
        send(SyncProtocol.encodeHello(serverName));
    }

    public boolean everReceivedAnything() {
        return receivedAnything;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player receiver, byte[] message) {
        if (!SyncProtocol.CHANNEL.equals(channel)) {
            return;
        }
        receivedAnything = true;
        SyncProtocol.DecodedPacket packet = SyncProtocol.decode(message);
        switch (packet.type()) {
            case VANISH_UPDATE -> dispatch(new VanishSyncEvent(packet.player(), packet.vanished(), packet.server()));
            case FULL_SYNC_RESPONSE -> packet.fullState().forEach((uuid, vanished) ->
                    dispatch(new VanishSyncEvent(uuid, vanished, packet.server())));
            case FULL_SYNC_REQUEST, HELLO -> { }
        }
    }

    private void dispatch(VanishSyncEvent event) {
        for (Consumer<VanishSyncEvent> handler : handlers) {
            handler.accept(event);
        }
    }

    private void send(byte[] payload) {
        Player carrier = Bukkit.getOnlinePlayers().stream().findAny().orElse(null);
        if (carrier == null) {
            return;
        }
        carrier.sendPluginMessage(plugin, SyncProtocol.CHANNEL, payload);
    }
}
