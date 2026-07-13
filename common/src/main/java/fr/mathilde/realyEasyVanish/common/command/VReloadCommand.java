package fr.mathilde.realyEasyVanish.common.command;

import fr.mathilde.realyEasyVanish.api.ReVanishCommandSource;
import fr.mathilde.realyEasyVanish.common.VanishManager;
import fr.mathilde.realyEasyVanish.common.config.ConfigManager;
import net.kyori.adventure.text.Component;

import java.util.List;

public final class VReloadCommand implements ReVanishCommand {

    private final VanishManager vanishManager;
    private final ConfigManager configManager;

    public VReloadCommand(VanishManager vanishManager, ConfigManager configManager) {
        this.vanishManager = vanishManager;
        this.configManager = configManager;
    }

    @Override
    public String name() {
        return "vreload";
    }

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public String permission() {
        return "revanish.vreload";
    }

    @Override
    public void execute(ReVanishCommandSource source, String[] args) {
        vanishManager.reload(configManager.load());
        source.sendMessage(Component.text("RealyEasyVanish configuration reloaded."));
    }

    @Override
    public List<String> tabComplete(ReVanishCommandSource source, String[] args) {
        return List.of();
    }
}
