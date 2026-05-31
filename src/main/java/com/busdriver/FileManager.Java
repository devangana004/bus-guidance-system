package com.busdriver;

import java.io.*;
import java.util.*;

/**
 * Handles TXT file reading and writing for repositories.
 */
public class FileManager {

    /**
     * Prevents creating FileManager objects because this is a utility class.
     */
    private FileManager() {
    }

    /**
     * Saves all records to a TXT file.
     */
    public static void writeAll(String filePath,
            String header, List<?> records) throws IOException {

        File file = new File(filePath);

        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(file, false))) {

            writer.write("# " + header);
            writer.newLine();
            writer.newLine();

            for (Object record : records) {
                writer.write(record.toString());
                writer.newLine();
            }
        }
    }

    /**
     * Reads all valid data lines from a TXT file.
     * Blank lines and comment lines are skipped.
     */
    public static List<String> readAll(String filePath)
            throws IOException {

        List<String> lines = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return lines;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (!line.isEmpty() && !line.startsWith("#")) {
                    lines.add(line);
                }
            }
        }

        return lines;
    }

    /**
     * Checks whether a file exists.
     */
    public static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * Deletes a file if it exists.
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);

        if (file.exists()) {
            return file.delete();
        }

        return false;
    }
}