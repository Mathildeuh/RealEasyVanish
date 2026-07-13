package fr.mathilde.realyEasyVanish.bukkit.proxy;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Reads spigot.yml / config/paper-global.yml straight off disk (both live at the server's working
 * directory on Spigot, Paper and Folia alike) instead of any platform API, so detection works
 * identically everywhere without risking a Paper-only class load on plain Spigot.
 */
public final class ProxyDetection {

    private ProxyDetection() {
    }

    public static boolean isBehindProxy() {
        return legacyBungeecordEnabled() || paperVelocityEnabled();
    }

    private static boolean legacyBungeecordEnabled() {
        return readBoolean(new File("spigot.yml"), "settings", "bungeecord");
    }

    private static boolean paperVelocityEnabled() {
        return readBoolean(new File("config/paper-global.yml"), "proxies", "velocity", "enabled");
    }

    private static boolean readBoolean(File file, String... path) {
        if (!file.isFile()) {
            return false;
        }
        try (InputStream in = new FileInputStream(file)) {
            Object node = new Yaml().load(in);
            for (String key : path) {
                if (!(node instanceof Map<?, ?> map)) {
                    return false;
                }
                node = map.get(key);
            }
            return node instanceof Boolean value && value;
        } catch (IOException e) {
            return false;
        }
    }
}
