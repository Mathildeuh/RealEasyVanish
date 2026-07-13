package fr.mathilde.realyEasyVanish.common.command;

import fr.mathilde.realyEasyVanish.api.ReVanishCommandSource;
import fr.mathilde.realyEasyVanish.api.VanishPlatform;
import fr.mathilde.realyEasyVanish.common.VanishManager;
import net.kyori.adventure.text.Component;

import java.util.List;

public final class VScoreboardCommand implements ReVanishCommand {

    private final VanishManager vanishManager;
    private final VanishPlatform platform;

    public VScoreboardCommand(VanishManager vanishManager, VanishPlatform platform) {
        this.vanishManager = vanishManager;
        this.platform = platform;
    }

    @Override
    public String name() {
        return "vscoreboard";
    }

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public String permission() {
        return "revanish.vscoreboard";
    }

    @Override
    public void execute(ReVanishCommandSource source, String[] args) {
        if (platform.isProxy()) {
            source.sendMessage(Component.text("The vanish sidebar is only available on backend servers."));
            return;
        }
        if (!source.isPlayer()) {
            source.sendMessage(Component.text("Only players can use this command."));
            return;
        }
        boolean enabled = vanishManager.toggleScoreboard(source.playerUuid());
        source.sendMessage(Component.text(enabled ? "Vanish sidebar enabled." : "Vanish sidebar disabled."));
    }

    @Override
    public List<String> tabComplete(ReVanishCommandSource source, String[] args) {
        return List.of();
    }
}
