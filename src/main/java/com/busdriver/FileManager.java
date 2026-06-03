package com.busdriver;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * FileManager utility class.
 * Handles all file read/write for DriverRepository and BusRepository.
 */
public class FileManager {

    public static String readFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
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

    public static List<String> readAll(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) return new ArrayList<>();
            List<String> lines = new ArrayList<>();
            for (String line : Files.readAllLines(path)) {
                if (!line.startsWith("#") && !line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
            return lines;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

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
    /**
     * Deletes a file at the given path.
     */
    public static void deleteFile(String filePath) {
        try {
            java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(filePath));
        } catch (IOException e) {
            // ignore
        }
    }
}