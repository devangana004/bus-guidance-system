package com.busdriver;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * FileManager utility class.
 * Handles all file read/write operations for DriverRepository and BusRepository.
 * Pavi's implementation.
 */
public class FileManager {

    /**
     * Reads entire file and returns content as a single String.
     */
    public static String readFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Writes a single String content to file.
     */
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

    /**
     * Reads all lines from a file.
     * Returns empty list if file doesn't exist.
     */
    public static List<String> readAll(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) return new ArrayList<>();
            List<String> lines = new ArrayList<>();
            for (String line : Files.readAllLines(path)) {
                // skip header/comment lines starting with #
                if (!line.startsWith("#") && !line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
            return lines;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Writes a list of objects to file with a header comment.
     * Each object's toString() is written as one line.
     */
    public static void writeAll(String filePath, String header,
                                 List<?> items) throws IOException {
        Path path = Paths.get(filePath);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(header).append("\n");
        for (Object item : items) {
            sb.append(item.toString()).append("\n");
        }
        Files.writeString(path, sb.toString());
    }
}