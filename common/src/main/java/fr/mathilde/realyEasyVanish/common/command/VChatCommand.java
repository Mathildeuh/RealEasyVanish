package fr.mathilde.realyEasyVanish.common.command;

import fr.mathilde.realyEasyVanish.api.ReVanishCommandSource;
import fr.mathilde.realyEasyVanish.common.VanishManager;
import net.kyori.adventure.text.Component;

import java.util.List;

public final class VChatCommand implements ReVanishCommand {

    private final VanishManager vanishManager;

    public VChatCommand(VanishManager vanishManager) {
        this.vanishManager = vanishManager;
    }

    @Override
    public String name() {
        return "vchat";
    }

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public String permission() {
        return "revanish.vchat";
    }

    @Override
    public void execute(ReVanishCommandSource source, String[] args) {
        if (!source.isPlayer()) {
            source.sendMessage(Component.text("Only players can use this command."));
            return;
        }
        if (!vanishManager.config().chatConfirmEnabled()) {
            source.sendMessage(Component.text("The chat-confirm module is disabled in the configuration."));
            return;
        }
        boolean blocked = !vanishManager.stateOf(source.playerUuid()).chatBlocked();
        vanishManager.setChatBlocked(source.playerUuid(), blocked);
        source.sendMessage(Component.text(blocked
                ? "Your chat messages are now blocked while vanished."
                : "Your chat messages will now be sent normally while vanished."));
    }

    @Override
    public List<String> tabComplete(ReVanishCommandSource source, String[] args) {
        return List.of();
    }
}
