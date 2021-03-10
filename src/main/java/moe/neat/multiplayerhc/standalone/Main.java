package moe.neat.multiplayerhc.standalone;

import moe.neat.multiplayerhc.ConfigManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    private static OffsetDateTime now = OffsetDateTime.now();
    private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

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

    private static void checkIfDirExists(Path dir) {
        if (!dir.toFile().exists()) {
            if (!dir.toFile().mkdir()) {
                System.out.println("Failed to create directory: " + dir.toAbsolutePath());
            }
        }
    }
}
