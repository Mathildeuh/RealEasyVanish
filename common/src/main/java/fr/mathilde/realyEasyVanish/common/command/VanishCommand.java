package fr.mathilde.realyEasyVanish.common.command;

import fr.mathilde.realyEasyVanish.api.ReVanishCommandSource;
import fr.mathilde.realyEasyVanish.api.ReVanishPlayer;
import fr.mathilde.realyEasyVanish.api.VanishPlatform;
import fr.mathilde.realyEasyVanish.common.VanishManager;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class VanishCommand implements ReVanishCommand {

    private final VanishManager vanishManager;
    private final VanishPlatform platform;

    public VanishCommand(VanishManager vanishManager, VanishPlatform platform) {
        this.vanishManager = vanishManager;
        this.platform = platform;
    }

    @Override
    public String name() {
        return "vanish";
    }

    @Override
    public List<String> aliases() {
        return List.of("v");
    }

    @Override
    public String permission() {
        return "revanish.vanish";
    }

    @Override
    public void execute(ReVanishCommandSource source, String[] args) {
        UUID targetUuid;
        String targetName;
        if (args.length > 0) {
            if (!source.hasPermission("revanish.vanish.others")) {
                source.sendMessage(Component.text("You cannot vanish other players."));
                return;
            }
            Optional<ReVanishPlayer> target = platform.player(args[0]);
            if (target.isEmpty()) {
                source.sendMessage(Component.text("Player not found: " + args[0]));
                return;
            }
            targetUuid = target.get().uuid();
            targetName = target.get().name();
        } else {
            if (!source.isPlayer()) {
                source.sendMessage(Component.text("Console must specify a player."));
                return;
            }
            targetUuid = source.playerUuid();
            targetName = source.name();
        }

        boolean vanished = vanishManager.toggle(targetUuid);
        source.sendMessage(Component.text((vanished ? "Vanished " : "Unvanished ") + targetName + "."));
    }

    @Override
    public List<String> tabComplete(ReVanishCommandSource source, String[] args) {
        if (args.length == 1) {
            return platform.onlinePlayers().stream().map(ReVanishPlayer::name).toList();
        }
        return List.of();
    }
}
