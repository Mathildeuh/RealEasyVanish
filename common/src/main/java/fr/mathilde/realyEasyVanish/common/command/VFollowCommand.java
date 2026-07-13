package fr.mathilde.realyEasyVanish.common.command;

import fr.mathilde.realyEasyVanish.api.ReVanishCommandSource;
import fr.mathilde.realyEasyVanish.api.ReVanishPlayer;
import fr.mathilde.realyEasyVanish.api.VanishPlatform;
import fr.mathilde.realyEasyVanish.common.VanishManager;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class VFollowCommand implements ReVanishCommand {

    private final VanishManager vanishManager;
    private final VanishPlatform platform;

    public VFollowCommand(VanishManager vanishManager, VanishPlatform platform) {
        this.vanishManager = vanishManager;
        this.platform = platform;
    }

    @Override
    public String name() {
        return "vfollow";
    }

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public String permission() {
        return "revanish.vfollow";
    }

    @Override
    public void execute(ReVanishCommandSource source, String[] args) {
        if (platform.isProxy()) {
            source.sendMessage(Component.text("Run this command on a backend server."));
            return;
        }
        if (!source.isPlayer()) {
            source.sendMessage(Component.text("Only players can use this command."));
            return;
        }
        if (args.length == 0) {
            source.sendMessage(Component.text("Usage: /vfollow <player|stop>"));
            return;
        }
        if (args[0].equalsIgnoreCase("stop")) {
            vanishManager.setFollowTarget(source.playerUuid(), null);
            source.sendMessage(Component.text("Stopped following."));
            return;
        }
        Optional<ReVanishPlayer> target = platform.player(args[0]);
        if (target.isEmpty()) {
            source.sendMessage(Component.text("Player not found: " + args[0]));
            return;
        }
        vanishManager.setFollowTarget(source.playerUuid(), target.get().uuid());
        source.sendMessage(Component.text("Now silently following " + target.get().name() + "."));
    }

    @Override
    public List<String> tabComplete(ReVanishCommandSource source, String[] args) {
        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            names.add("stop");
            platform.onlinePlayers().forEach(p -> names.add(p.name()));
            return names;
        }
        return List.of();
    }
}
