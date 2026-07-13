package fr.mathilde.realyEasyVanish.velocity.sync;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import fr.mathilde.realyEasyVanish.api.SyncBridge;
import fr.mathilde.realyEasyVanish.api.VanishSyncEvent;
import fr.mathilde.realyEasyVanish.common.sync.SyncPacketType;
import fr.mathilde.realyEasyVanish.common.sync.SyncProtocol;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * The proxy is the authoritative hub: a VANISH_UPDATE coming from one backend is applied locally
 * (to the proxy's own network-wide VanishManager) and then rebroadcast to every other backend, so
 * backends never need to talk to each other directly.
 */
public final class VelocitySyncBridge implements SyncBridge {

    public static final ChannelIdentifier CHANNEL = MinecraftChannelIdentifier.from(SyncProtocol.CHANNEL);

    private final ProxyServer proxyServer;
    private final List<Consumer<VanishSyncEvent>> handlers = new CopyOnWriteArrayList<>();
    private final List<Consumer<String>> serverConfirmedHandlers = new CopyOnWriteArrayList<>();

    public VelocitySyncBridge(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
        proxyServer.getChannelRegistrar().register(CHANNEL);
    }

    /**
     * Fired for the actual backend a message physically came through (resolved from the Velocity
     * connection itself, not the packet's self-reported server name), for any packet type -
     * including HELLO, so an installed-but-idle backend still gets confirmed quickly.
     */
    public void onServerConfirmed(Consumer<String> handler) {
        serverConfirmedHandlers.add(handler);
    }

    @Override
    public void publishVanishState(UUID player, boolean vanished, String sourceServer) {
        broadcastToBackendsExcept(SyncProtocol.encodeVanishUpdate(player, vanished, sourceServer), sourceServer);
    }

    @Override
    public void requestFullSync(String targetServer) {
        // No-op here: the proxy is authoritative and pushes full state proactively whenever a
        // player connects to a backend (see ReVanishVelocityPlugin#onServerConnected).
    }

    @Override
    public void onStateReceived(Consumer<VanishSyncEvent> handler) {
        handlers.add(handler);
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getIdentifier().equals(CHANNEL)) {
            return;
        }
        event.setResult(PluginMessageEvent.ForwardResult.handled());
        if (event.getSource() instanceof ServerConnection connection) {
            String actualServerName = connection.getServerInfo().getName();
            for (Consumer<String> handler : serverConfirmedHandlers) {
                handler.accept(actualServerName);
            }
        }
        SyncProtocol.DecodedPacket packet = SyncProtocol.decode(event.getData());
        if (packet.type() != SyncPacketType.VANISH_UPDATE) {
            return;
        }
        VanishSyncEvent syncEvent = new VanishSyncEvent(packet.player(), packet.vanished(), packet.server());
        for (Consumer<VanishSyncEvent> handler : handlers) {
            handler.accept(syncEvent);
        }
        broadcastToBackendsExcept(
                SyncProtocol.encodeVanishUpdate(packet.player(), packet.vanished(), packet.server()),
                packet.server());
    }

    private void broadcastToBackendsExcept(byte[] payload, String excludedServer) {
        for (RegisteredServer server : proxyServer.getAllServers()) {
            if (excludedServer != null && server.getServerInfo().getName().equals(excludedServer)) {
                continue;
            }
            server.sendPluginMessage(CHANNEL, payload);
        }
    }
}
