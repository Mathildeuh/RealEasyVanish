package fr.mathilde.realyEasyVanish.api;

import java.util.List;
import java.util.UUID;

/**
 * Read-only view over vanish state used to resolve %revanish_...% placeholders without any I/O.
 */
public interface PlaceholderSource {

    boolean isVanished(UUID player);

    boolean pickupEnabled(UUID player);

    int vanishedCount();

    int visibleOnlineCount();

    String prefix();

    List<String> vanishedNames();

    List<String> visiblePlayerNames();
}
