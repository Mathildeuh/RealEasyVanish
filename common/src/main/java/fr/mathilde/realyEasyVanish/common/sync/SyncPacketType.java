package fr.mathilde.realyEasyVanish.common.sync;

public enum SyncPacketType {
    VANISH_UPDATE,
    FULL_SYNC_REQUEST,
    FULL_SYNC_RESPONSE,
    HELLO;

    public static SyncPacketType fromId(byte id) {
        SyncPacketType[] values = values();
        if (id < 0 || id >= values.length) {
            throw new IllegalArgumentException("Unknown sync packet id: " + id);
        }
        return values[id];
    }

    public byte id() {
        return (byte) ordinal();
    }
}
