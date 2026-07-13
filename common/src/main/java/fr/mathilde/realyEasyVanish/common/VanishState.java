package fr.mathilde.realyEasyVanish.common;

import java.util.UUID;

public record VanishState(
        boolean vanished,
        boolean pickupItems,
        boolean chatBlocked,
        UUID followTarget,
        UUID specTarget
) {

    public static VanishState defaults() {
        return new VanishState(false, false, true, null, null);
    }

    public VanishState withVanished(boolean value) {
        return new VanishState(value, pickupItems, chatBlocked, followTarget, specTarget);
    }

    public VanishState withPickupItems(boolean value) {
        return new VanishState(vanished, value, chatBlocked, followTarget, specTarget);
    }

    public VanishState withChatBlocked(boolean value) {
        return new VanishState(vanished, pickupItems, value, followTarget, specTarget);
    }

    public VanishState withFollowTarget(UUID target) {
        return new VanishState(vanished, pickupItems, chatBlocked, target, specTarget);
    }

    public VanishState withSpecTarget(UUID target) {
        return new VanishState(vanished, pickupItems, chatBlocked, followTarget, target);
    }
}
