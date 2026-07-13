package fr.mathilde.realyEasyVanish.common.sync;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Wire format for the "revanish:sync" plugin messaging channel. Self-contained (no Guava /
 * platform helper classes) so the exact same bytes are produced and read on both Bukkit and
 * Velocity.
 */
public final class SyncProtocol {

    public static final String CHANNEL = "revanish:sync";

    private SyncProtocol() {
    }

    public static byte[] encodeVanishUpdate(UUID player, boolean vanished, String sourceServer) {
        return write(SyncPacketType.VANISH_UPDATE, out -> {
            writeUuid(out, player);
            out.writeBoolean(vanished);
            out.writeUTF(sourceServer);
        });
    }

    public static byte[] encodeFullSyncRequest(String targetServer) {
        return write(SyncPacketType.FULL_SYNC_REQUEST, out -> out.writeUTF(targetServer));
    }

    /**
     * Sent once by a backend as soon as it has a player connection to carry it, purely so the
     * proxy can confirm the plugin is installed there even if nobody has ever vanished yet.
     */
    public static byte[] encodeHello(String sourceServer) {
        return write(SyncPacketType.HELLO, out -> out.writeUTF(sourceServer));
    }

    public static byte[] encodeFullSyncResponse(Map<UUID, Boolean> vanishedStates, String sourceServer) {
        return write(SyncPacketType.FULL_SYNC_RESPONSE, out -> {
            out.writeUTF(sourceServer);
            out.writeInt(vanishedStates.size());
            for (Map.Entry<UUID, Boolean> entry : vanishedStates.entrySet()) {
                writeUuid(out, entry.getKey());
                out.writeBoolean(entry.getValue());
            }
        });
    }

    public static DecodedPacket decode(byte[] payload) {
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(payload))) {
            SyncPacketType type = SyncPacketType.fromId(in.readByte());
            return switch (type) {
                case VANISH_UPDATE -> {
                    UUID uuid = readUuid(in);
                    boolean vanished = in.readBoolean();
                    String server = in.readUTF();
                    yield new DecodedPacket(type, uuid, vanished, server, null);
                }
                case FULL_SYNC_REQUEST, HELLO -> new DecodedPacket(type, null, false, in.readUTF(), null);
                case FULL_SYNC_RESPONSE -> {
                    String server = in.readUTF();
                    int count = in.readInt();
                    Map<UUID, Boolean> states = new HashMap<>(count);
                    for (int i = 0; i < count; i++) {
                        states.put(readUuid(in), in.readBoolean());
                    }
                    yield new DecodedPacket(type, null, false, server, states);
                }
            };
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static byte[] write(SyncPacketType type, IoConsumer<DataOutputStream> writer) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            out.writeByte(type.id());
            writer.accept(out);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return bytes.toByteArray();
    }

    private static void writeUuid(DataOutputStream out, UUID uuid) throws IOException {
        out.writeLong(uuid.getMostSignificantBits());
        out.writeLong(uuid.getLeastSignificantBits());
    }

    private static UUID readUuid(DataInputStream in) throws IOException {
        return new UUID(in.readLong(), in.readLong());
    }

    public record DecodedPacket(SyncPacketType type, UUID player, boolean vanished, String server,
                                 Map<UUID, Boolean> fullState) {
    }

    @FunctionalInterface
    private interface IoConsumer<T> {
        void accept(T t) throws IOException;
    }
}
