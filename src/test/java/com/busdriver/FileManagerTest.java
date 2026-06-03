package com.busdriver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * Unit tests for FileManager utility class.
 * Written by: Pavi
 */
public class FileManagerTest {

    private static final String TEST_FILE = "data/test_filemanager.txt";

    @BeforeEach
    void cleanUp() {
        FileManager.deleteFile(TEST_FILE);
    }

    @Test
    void testWriteAndReadFile() {
        FileManager.writeFile(TEST_FILE, "hello world");
        String content = FileManager.readFile(TEST_FILE);
        assertEquals("hello world", content);
    }

    @Test
    void testReadFile_missingFile_returnsEmpty() {
        String content = FileManager.readFile("data/nonexistent.txt");
        assertEquals("", content);
    }

    @Test
    void testWriteAll_andReadAll() throws Exception {
        List<String> items = List.of("line1", "line2", "line3");
        FileManager.writeAll(TEST_FILE, "Test Header", items);
        List<String> result = FileManager.readAll(TEST_FILE);
        assertEquals(3, result.size());
        assertEquals("line1", result.get(0));
    }

    @Test
    void testReadAll_emptyFile_returnsEmptyList() throws Exception {
        FileManager.writeFile(TEST_FILE, "");
        List<String> result = FileManager.readAll(TEST_FILE);
        assertTrue(result.isEmpty());
    }

    @Test
    void testReadAll_missingFile_returnsEmptyList() {
        List<String> result = FileManager.readAll("data/missing.txt");
        assertTrue(result.isEmpty());
    }

    @Test
    void testWriteAll_skipsHeaderInReadAll() throws Exception {
        List<String> items = List.of("data1", "data2");
        FileManager.writeAll(TEST_FILE, "This is a header", items);
        List<String> result = FileManager.readAll(TEST_FILE);
        assertFalse(result.contains("# This is a header"));
        assertEquals(2, result.size());
    }

    @Test
    void testDeleteFile_existingFile() {
        FileManager.writeFile(TEST_FILE, "some content");
        FileManager.deleteFile(TEST_FILE);
        String content = FileManager.readFile(TEST_FILE);
        assertEquals("", content);
    }

    @Test
    void testDeleteFile_nonExistingFile_noError() {
        assertDoesNotThrow(() -> FileManager.deleteFile("data/nothing.txt"));
    }

    @Test
    void testWriteFile_overwritesExistingContent() {
        FileManager.writeFile(TEST_FILE, "first content");
        FileManager.writeFile(TEST_FILE, "second content");
        String content = FileManager.readFile(TEST_FILE);
        assertEquals("second content", content);
    }

    @Test
    void testReadAll_ignoresBlankLines() throws Exception {
        FileManager.writeFile(TEST_FILE, "line1\n\n\nline2\n");
        List<String> result = FileManager.readAll(TEST_FILE);
        assertEquals(2, result.size());
    }

    @Test
    void testWriteAll_multipleItems() throws Exception {
        List<String> items = List.of("a", "b", "c", "d", "e");
        FileManager.writeAll(TEST_FILE, "Header", items);
        List<String> result = FileManager.readAll(TEST_FILE);
        assertEquals(5, result.size());
    }

    @Test
    void testWriteFile_createsDirectoryIfMissing() {
        String deepPath = "data/subdir/test.txt";
        assertDoesNotThrow(() -> FileManager.writeFile(deepPath, "test"));
        FileManager.deleteFile(deepPath);
    }
}
