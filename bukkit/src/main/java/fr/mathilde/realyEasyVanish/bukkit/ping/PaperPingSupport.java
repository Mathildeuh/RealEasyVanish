package fr.mathilde.realyEasyVanish.bukkit.ping;

public final class PaperPingSupport {

    private static final boolean AVAILABLE = detect();

    private PaperPingSupport() {
    }

    public static boolean isAvailable() {
        return AVAILABLE;
    }

    private static boolean detect() {
        try {
            Class.forName("com.destroystokyo.paper.event.server.PaperServerListPingEvent");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
