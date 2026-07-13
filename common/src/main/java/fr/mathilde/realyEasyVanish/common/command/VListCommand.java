package fr.mathilde.realyEasyVanish.common.command;

import fr.mathilde.realyEasyVanish.api.ReVanishCommandSource;
import fr.mathilde.realyEasyVanish.api.VanishPlatform;
import fr.mathilde.realyEasyVanish.common.VanishManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.UUID;

public final class VListCommand implements ReVanishCommand {

    private final VanishManager vanishManager;
    private final VanishPlatform platform;

    public VListCommand(VanishManager vanishManager, VanishPlatform platform) {
        this.vanishManager = vanishManager;
        this.platform = platform;
    }

    @Override
    public String name() {
        return "vlist";
    }

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public String permission() {
        return "revanish.vlist";
    }

    @Override
    public void execute(ReVanishCommandSource source, String[] args) {
        List<UUID> vanished = List.copyOf(vanishManager.vanishedUuids());
        if (vanished.isEmpty()) {
            source.sendMessage(Component.text("No players are currently vanished.", NamedTextColor.GRAY));
            return;
        }
        source.sendMessage(Component.text("Vanished players (" + vanished.size() + "):", NamedTextColor.GOLD));
        for (UUID uuid : vanished) {
            platform.player(uuid).ifPresent(player -> source.sendMessage(
                    Component.text("  - " + player.name(), NamedTextColor.GREEN)
                            .hoverEvent(HoverEvent.showText(Component.text("Click to run /vfollow " + player.name())))
                            .clickEvent(ClickEvent.runCommand("/vfollow " + player.name()))
            ));
        }
    }

    @Override
    public List<String> tabComplete(ReVanishCommandSource source, String[] args) {
        return List.of();
    }
}
