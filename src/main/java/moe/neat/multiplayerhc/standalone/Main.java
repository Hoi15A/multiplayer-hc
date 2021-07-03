package moe.neat.multiplayerhc.standalone;

import moe.neat.multiplayerhc.ConfigManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entrypoint for the standalone part of Multiplayer Hardcore.
 * Used to perform world moving as this cannot be done at server runtime.
 */
public class Main {
    private static final OffsetDateTime now = OffsetDateTime.now();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {
        logger.info("Running in standalone mode");
        String reset = ConfigManager.read("resetWorld");
        String worlds = ConfigManager.read("worldFolder");
        String backupDir = ConfigManager.read("backupFolder");


        if ("true".equals(reset)) {
            for (String folder : worlds.split(", ?")) {
                moveWorld(Paths.get(folder), Paths.get(backupDir));
            }
            ConfigManager.save("resetWorld", "false");
        }
    }

    /**
     * Moves a given world folder to a destination directory.
     * The worlds will be put in a folder inside the destination that uses the current time as the folder name.
     *
     * @param folder World folder
     * @param dest Backup folder
     */
    private static void moveWorld(Path folder, Path dest) {
        checkIfDirExists(dest);
        dest = Paths.get(dest.toAbsolutePath() + "/" + formatter.format(now));
        checkIfDirExists(dest);

        try {
            logger.log(Level.INFO, "Moving {0} to the backup folder.", dest);
            Files.move(folder, Paths.get(dest.toAbsolutePath().toString(), folder.getFileName().toString()));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to move {0}", folder.toAbsolutePath());
            e.printStackTrace();
        }

    }

    /**
     * Checks if a directory exists and if not tries to create it
     *
     * @param dir Directory
     */
    private static void checkIfDirExists(Path dir) {
        if (!dir.toFile().exists() && !dir.toFile().mkdir()) {
            logger.log(Level.SEVERE, "Failed to create directory: {0}", dir.toAbsolutePath());
        }
    }
}
