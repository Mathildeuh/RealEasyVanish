package fr.mathilde.realyEasyVanish.bukkit.placeholder;

import fr.mathilde.realyEasyVanish.common.VanishManager;
import fr.mathilde.realyEasyVanish.common.placeholder.PlaceholderResolver;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class ReVanishPlaceholderExpansion extends PlaceholderExpansion {

    private final Plugin plugin;
    private final VanishManager vanishManager;

    public ReVanishPlaceholderExpansion(Plugin plugin, VanishManager vanishManager) {
        this.plugin = plugin;
        this.vanishManager = vanishManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "revanish";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Mathildeuh";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(@Nullable OfflinePlayer player, @NotNull String params) {
        UUID uuid = player == null ? null : player.getUniqueId();
        return PlaceholderResolver.resolve(vanishManager, uuid, params);
    }
}
