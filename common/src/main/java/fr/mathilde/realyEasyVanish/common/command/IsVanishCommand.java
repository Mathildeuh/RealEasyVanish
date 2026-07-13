package fr.mathilde.realyEasyVanish.common.command;

import fr.mathilde.realyEasyVanish.api.ReVanishCommandSource;
import fr.mathilde.realyEasyVanish.api.ReVanishPlayer;
import fr.mathilde.realyEasyVanish.api.VanishPlatform;
import fr.mathilde.realyEasyVanish.common.VanishManager;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Optional;

public final class IsVanishCommand implements ReVanishCommand {

    private final VanishManager vanishManager;
    private final VanishPlatform platform;

    public IsVanishCommand(VanishManager vanishManager, VanishPlatform platform) {
        this.vanishManager = vanishManager;
        this.platform = platform;
    }

    @Override
    public String name() {
        return "isvanish";
    }

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public String permission() {
        return "revanish.isvanish";
    }

    @Override
    public void execute(ReVanishCommandSource source, String[] args) {
        if (args.length == 0) {
            source.sendMessage(Component.text("Usage: /isvanish <player>"));
            return;
        }
        Optional<ReVanishPlayer> target = platform.player(args[0]);
        if (target.isEmpty()) {
            source.sendMessage(Component.text("Player not found: " + args[0]));
            return;
        }
        boolean vanished = vanishManager.isVanished(target.get().uuid());
        source.sendMessage(Component.text(target.get().name() + " is " + (vanished ? "vanished." : "visible.")));
    }

    @Override
    public List<String> tabComplete(ReVanishCommandSource source, String[] args) {
        if (args.length == 1) {
            return platform.onlinePlayers().stream().map(ReVanishPlayer::name).toList();
        }
        return List.of();
    }
}
