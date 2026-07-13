package fr.mathilde.realyEasyVanish.api;

import java.util.UUID;

public record VanishSyncEvent(UUID player, boolean vanished, String sourceServer) {
}
