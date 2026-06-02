package com.busdriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles simple TXT/JSON file read and write operations for repositories.
 */
public class FileManager {

    public static String readFile(String filePath) {
        try {
            Path path = Path.of(filePath);

            if (!Files.exists(path)) {
                return "";
            }

            return Files.readString(path);
        } catch (IOException e) {
            return "";
        }
    }

    public static void writeFile(String filePath, String content) {
        try {
            Path path = Path.of(filePath);

            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            Files.writeString(path, content);
        } catch (IOException e) {
            throw new RuntimeException("File write failed: " + e.getMessage());
        }
    }
}