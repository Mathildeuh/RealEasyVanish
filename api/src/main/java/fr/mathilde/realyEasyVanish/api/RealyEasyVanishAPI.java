package fr.mathilde.realyEasyVanish.api;

import java.util.Optional;

/**
 * Static entry point for third-party plugins running in the same JVM (Bukkit backend or Velocity
 * proxy). Populated by the platform plugin on enable and cleared on disable — never held onto
 * across a reload, always re-fetch via {@link #get()}.
 */
public final class RealyEasyVanishAPI {

    private static volatile PlaceholderSource instance;

    private RealyEasyVanishAPI() {
    }

    public static void register(PlaceholderSource source) {
        instance = source;
    }

    public static void unregister(PlaceholderSource source) {
        if (instance == source) {
            instance = null;
        }
    }

    public static Optional<PlaceholderSource> get() {
        return Optional.ofNullable(instance);
    }

    public static boolean isLoaded() {
        return instance != null;
    }
}
