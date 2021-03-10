package moe.neat.multiplayerhc;

import moe.neat.multiplayerhc.standalone.Main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

public class ConfigManager {

    private static final String CONFIG_DIR_NAME = "MultiplayerHc";
    private static final String CONFIG_FILE_NAME = "config.properties";

    private static final Properties prop = new Properties();

    private static final Path jar = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    private static final Path configDir = jar.getParent().resolve(CONFIG_DIR_NAME);
    private static final Path config = configDir.resolve(CONFIG_FILE_NAME);

    static {

        if (!configDir.toFile().exists()) {
            if (!configDir.toFile().mkdir()) {
                System.out.println("Failed to create config dir.");
            }
        }

        if (!config.toFile().exists()) {
            try {
                Files.createFile(config);
                Files.write(config, Objects.requireNonNull(ConfigManager.class.getClassLoader()
                        .getResourceAsStream(CONFIG_FILE_NAME))
                        .readAllBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            prop.load(Files.newInputStream(config));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save(String key, String value) throws IOException {
        prop.setProperty(key, value);
        prop.store(Files.newOutputStream(config), null);
    }

    public static String read(String key) {
        return (String) prop.get(key);
    }
}
