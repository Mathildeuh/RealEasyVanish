package fr.mathilde.realyEasyVanish.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Handles the on-disk config.yml only. The bundled default values are read from the plugin
 * jar itself (common ships its own config.yml resource), so this store never needs to know
 * about platform-specific resource loaders.
 */
public interface ConfigStore {

    boolean exists();

    InputStream openRead() throws IOException;

    OutputStream openWrite() throws IOException;
}
