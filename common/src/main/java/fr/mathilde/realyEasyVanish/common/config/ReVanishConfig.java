package fr.mathilde.realyEasyVanish.common.config;

public record ReVanishConfig(
        String vanishPrefix,
        boolean chatConfirmEnabled,
        long scoreboardUpdateIntervalTicks,
        long followUpdateIntervalTicks,
        String serverName
) {

    public static ReVanishConfig defaults() {
        return new ReVanishConfig("&7[Vanish] &r", true, 20L, 2L, "server");
    }
}
