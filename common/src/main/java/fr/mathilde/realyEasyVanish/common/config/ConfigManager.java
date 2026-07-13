package fr.mathilde.realyEasyVanish.common.config;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public final class ConfigManager {

    private final ConfigStore store;

    public ConfigManager(ConfigStore store) {
        this.store = store;
    }

    public ReVanishConfig load() {
        try {
            ensureDefaultCopied();
            try (InputStream in = store.openRead()) {
                Map<String, Object> data = new Yaml().load(in);
                return data == null ? ReVanishConfig.defaults() : parse(data);
            }
        } catch (IOException e) {
            return ReVanishConfig.defaults();
        }
    }

    private void ensureDefaultCopied() throws IOException {
        if (store.exists()) {
            return;
        }
        try (InputStream defaults = ConfigManager.class.getResourceAsStream("/config.yml")) {
            if (defaults == null) {
                return;
            }
            try (OutputStream out = store.openWrite()) {
                defaults.transferTo(out);
            }
        }
    }

    private ReVanishConfig parse(Map<String, Object> data) {
        ReVanishConfig defaults = ReVanishConfig.defaults();
        return new ReVanishConfig(
                str(data.get("vanish-prefix"), defaults.vanishPrefix()),
                bool(data.get("chat-confirm-enabled"), defaults.chatConfirmEnabled()),
                num(data.get("scoreboard-update-interval-ticks"), defaults.scoreboardUpdateIntervalTicks()),
                num(data.get("follow-update-interval-ticks"), defaults.followUpdateIntervalTicks()),
                str(data.get("server-name"), defaults.serverName())
        );
    }

    private static String str(Object value, String fallback) {
        return value == null ? fallback : value.toString();
    }

    private static boolean bool(Object value, boolean fallback) {
        return value instanceof Boolean b ? b : fallback;
    }

    private static long num(Object value, long fallback) {
        return value instanceof Number n ? n.longValue() : fallback;
    }
}
