package fr.mathilde.realyEasyVanish.bukkit.config;

import fr.mathilde.realyEasyVanish.common.config.ConfigStore;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class BukkitConfigStore implements ConfigStore {

    private final File file;

    public BukkitConfigStore(Plugin plugin) {
        this.file = new File(plugin.getDataFolder(), "config.yml");
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public InputStream openRead() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public OutputStream openWrite() throws IOException {
        File parent = file.getParentFile();
        if (!parent.isDirectory() && !parent.mkdirs() && !parent.isDirectory()) {
            throw new IOException("Could not create directory: " + parent);
        }
        return new FileOutputStream(file);
    }
}
