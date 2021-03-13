package moe.neat.multiplayerhc.standalone;

import moe.neat.multiplayerhc.ConfigManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entrypoint for the standalone part of Multiplayer Hardcore.
 * Used to perform world moving as this cannot be done at server runtime.
 */
public class Main {
    private static final OffsetDateTime now = OffsetDateTime.now();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    public static void main(String[] args) throws IOException {
        System.out.println("Running in standalone mode");
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
        dest = Paths.get(dest.toAbsolutePath().toString() + "/" + formatter.format(now));
        checkIfDirExists(dest);

        try {
            System.out.println("Moving " + dest.toString() + " to the backup folder.");
            Files.move(folder, Paths.get(dest.toAbsolutePath().toString(), folder.getFileName().toString()));
        } catch (IOException e) {
            System.out.println("Failed to move " + folder.toAbsolutePath());
            e.printStackTrace();
        }

    }

    /**
     * Checks if a directory exists and if not tries to create it
     *
     * @param dir Directory
     */
    private static void checkIfDirExists(Path dir) {
        if (!dir.toFile().exists()) {
            if (!dir.toFile().mkdir()) {
                System.out.println("Failed to create directory: " + dir.toAbsolutePath());
            }
        }
    }
}
