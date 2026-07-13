package fr.mathilde.realyEasyVanish.velocity.config;

import fr.mathilde.realyEasyVanish.common.config.ConfigStore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class VelocityConfigStore implements ConfigStore {

    private final Path file;

    public VelocityConfigStore(Path dataDirectory) {
        this.file = dataDirectory.resolve("config.yml");
    }

    @Override
    public boolean exists() {
        return Files.exists(file);
    }

    @Override
    public InputStream openRead() throws IOException {
        return Files.newInputStream(file);
    }

    @Override
    public OutputStream openWrite() throws IOException {
        Files.createDirectories(file.getParent());
        return Files.newOutputStream(file);
    }
}
