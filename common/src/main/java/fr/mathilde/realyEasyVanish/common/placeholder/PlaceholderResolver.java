package fr.mathilde.realyEasyVanish.common.placeholder;

import fr.mathilde.realyEasyVanish.api.PlaceholderSource;

import java.util.UUID;

/**
 * Shared logic behind the %revanish_...% placeholders, so the PAPI expansion in :bukkit is a
 * thin wrapper. viewer may be null for placeholders that don't need a specific player.
 */
public final class PlaceholderResolver {

    private PlaceholderResolver() {
    }

    public static String resolve(PlaceholderSource source, UUID viewer, String params) {
        return switch (params) {
            case "is_vanished" -> viewer != null && source.isVanished(viewer) ? "Yes" : "No";
            case "is_vanished_bool" -> String.valueOf(viewer != null && source.isVanished(viewer));
            case "vanished_count" -> String.valueOf(source.vanishedCount());
            case "visible_online" -> String.valueOf(source.visibleOnlineCount());
            case "prefix" -> source.prefix();
            case "pickup" -> viewer != null && source.pickupEnabled(viewer) ? "Yes" : "No";
            case "vanished_list" -> String.join(", ", source.vanishedNames());
            case "visible_player_list" -> String.join(", ", source.visiblePlayerNames());
            default -> null;
        };
    }
}
