package com.busdriver;

import org.junit.jupiter.api.*;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * FileManagerTest.java
 * Unit tests for FileManager utility class.
 * Tests all file read/write operations.
 *
 * Contributor: Pavithra Gokul Salini (s4146962)
 * Group 52 | ISYS3413 | Assignment 4
 */
@DisplayName("FileManager Unit Tests — Pavithra Gokul Salini (s4146962)")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileManagerTest {

    static final String BASE = "data/pavi_test_";
    String file(int n) { return BASE + n + ".txt"; }

    @AfterEach
    void cleanup() {
        for (int i = 1; i <= 12; i++) FileManager.deleteFile(file(i));
    }

    @Test @Order(1)
    @DisplayName("TC01 Normal: writeAll saves records to TXT file correctly")
    void tc01_writeAllSavesCorrectly() throws Exception {
        List<Bus> buses = List.of(new Bus("12345678", 40, 75.0, "Diesel"));
        FileManager.writeAll(file(1),
            "Bus Data - Format: busID|capacity|fuelLevel|fuelType", buses);
        assertTrue(new File(file(1)).exists(), "File should exist after writeAll.");
        assertTrue(new File(file(1)).length() > 0, "File should not be empty.");
    }

    @Test @Order(2)
    @DisplayName("TC02 Normal: readAll retrieves all saved records correctly")
    void tc02_readAllRetrievesRecords() throws Exception {
        List<Bus> buses = Arrays.asList(
            new Bus("11111111", 30, 80.0, "Diesel"),
            new Bus("22222222", 40, 70.0, "Hybrid"));
        FileManager.writeAll(file(2),
            "Bus Data - Format: busID|capacity|fuelLevel|fuelType", buses);
        List<String> lines = FileManager.readAll(file(2));
        assertEquals(2, lines.size(), "Should retrieve 2 records.");
        assertTrue(lines.get(0).startsWith("11111111"));
    }

    @Test @Order(3)
    @DisplayName("TC03 Edge: readAll returns empty list when file does not exist")
    void tc03_readAllMissingFile() throws IOException {
        List<String> lines = FileManager.readAll(file(3));
        assertNotNull(lines);
        assertTrue(lines.isEmpty(),
            "Should return empty list for missing file.");
    }

    @Test @Order(4)
    @DisplayName("TC04 Edge: readAll returns empty list when file is empty")
    void tc04_readAllEmptyFile() throws IOException {
        new File("data").mkdirs();
        new File(file(4)).createNewFile();
        List<String> lines = FileManager.readAll(file(4));
        assertTrue(lines.isEmpty(), "Empty file should return empty list.");
    }

    @Test @Order(5)
    @DisplayName("TC05 Edge: readAll skips comment lines starting with #")
    void tc05_readAllSkipsComments() throws Exception {
        FileManager.writeAll(file(5),
            "Bus Data - Format: busID|capacity|fuelLevel|fuelType",
            List.of(new Bus("33333333", 40, 75.0, "Diesel")));
        List<String> lines = FileManager.readAll(file(5));
        assertEquals(1, lines.size(),
            "Only 1 real record — comment line should be skipped.");
        assertFalse(lines.get(0).startsWith("#"));
    }

    @Test @Order(6)
    @DisplayName("TC06 Edge: readAll skips blank lines in file gracefully")
    void tc06_readAllSkipsBlanks() throws IOException {
        new File("data").mkdirs();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file(6)))) {
            w.write("44444444|30|60.0|Hybrid"); w.newLine();
            w.newLine();
            w.write("   "); w.newLine();
            w.write("55555555|50|90.0|Diesel"); w.newLine();
        }
        List<String> lines = FileManager.readAll(file(6));
        assertEquals(2, lines.size(), "Blank lines should be skipped.");
    }

    @Test @Order(7)
    @DisplayName("TC07 Normal: writeAll completely overwrites existing content")
    void tc07_writeAllOverwrites() throws Exception {
        FileManager.writeAll(file(7), "header",
            List.of(new Bus("11111111", 30, 80.0, "Diesel")));
        FileManager.writeAll(file(7), "header", Arrays.asList(
            new Bus("99999999", 40, 70.0, "Hybrid"),
            new Bus("88888888", 50, 60.0, "Electricity")));
        List<String> lines = FileManager.readAll(file(7));
        assertEquals(2, lines.size(), "Should have 2 new records.");
        assertTrue(lines.get(0).startsWith("99999999"),
            "Old content should be replaced.");
    }

    @Test @Order(8)
    @DisplayName("TC08 Normal: deleteFile removes the TXT file from disk")
    void tc08_deleteFileRemoves() throws Exception {
        FileManager.writeAll(file(8), "header",
            List.of(new Bus("12121212", 30, 80.0, "Diesel")));
        assertTrue(new File(file(8)).exists());
        FileManager.deleteFile(file(8));
        assertFalse(new File(file(8)).exists(),
            "File should not exist after deleteFile.");
    }

    @Test @Order(9)
    @DisplayName("TC09 Edge: deleteFile returns false when file does not exist")
    void tc09_deleteFileMissing() {
        boolean result = FileManager.deleteFile(file(9));
        assertFalse(result,
            "deleteFile should return false for missing file.");
    }

    @Test @Order(10)
    @DisplayName("TC10 Normal: fileExists returns true when file exists")
    void tc10_fileExistsTrue() throws Exception {
        FileManager.writeAll(file(10), "header",
            List.of(new Bus("34343434", 40, 80.0, "Diesel")));
        assertTrue(FileManager.fileExists(file(10)));
    }

    @Test @Order(11)
    @DisplayName("TC11 Edge: fileExists returns false when file does not exist")
    void tc11_fileExistsFalse() {
        assertFalse(FileManager.fileExists(file(11)));
    }

    @Test @Order(12)
    @DisplayName("TC12 Normal: write then read round-trip preserves all values")
    void tc12_writeReadRoundTrip() throws Exception {
        Bus original = new Bus("56565656", 45, 88.5, "Electricity");
        FileManager.writeAll(file(12),
            "Bus Data - Format: busID|capacity|fuelLevel|fuelType",
            List.of(original));
        List<String> lines = FileManager.readAll(file(12));
        assertEquals(1, lines.size());
        String line = lines.get(0);
        assertTrue(line.contains("56565656"));
        assertTrue(line.contains("45"));
        assertTrue(line.contains("88.5"));
        assertTrue(line.contains("Electricity"));
    }
}