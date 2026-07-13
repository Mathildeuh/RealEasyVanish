package fr.mathilde.realyEasyVanish.common.command;

import fr.mathilde.realyEasyVanish.api.ReVanishCommandSource;

import java.util.List;

public interface ReVanishCommand {

    String name();

    List<String> aliases();

    String permission();

    void execute(ReVanishCommandSource source, String[] args);

    List<String> tabComplete(ReVanishCommandSource source, String[] args);
}
