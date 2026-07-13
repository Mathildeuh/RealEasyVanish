package fr.mathilde.realyEasyVanish.velocity.command;

import com.velocitypowered.api.command.SimpleCommand;
import fr.mathilde.realyEasyVanish.common.command.ReVanishCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

public final class VelocitySimpleCommand implements SimpleCommand {

    private final ReVanishCommand command;

    public VelocitySimpleCommand(ReVanishCommand command) {
        this.command = command;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!invocation.source().hasPermission(command.permission())) {
            invocation.source().sendMessage(Component.text("You do not have permission to use this command.", NamedTextColor.RED));
            return;
        }
        command.execute(new VelocityCommandSource(invocation.source()), invocation.arguments());
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return command.tabComplete(new VelocityCommandSource(invocation.source()), invocation.arguments());
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission(command.permission());
    }
}
