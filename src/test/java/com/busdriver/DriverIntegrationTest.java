package com.busdriver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

/**
 * Integration tests for Driver operations.
 * Tests use real TXT files and real class implementations.
 * Written by: Devangana
 */
public class DriverIntegrationTest {

    private static final String TEST_FILE = "data/integration_drivers.txt";

    @BeforeEach
    void cleanUp() {
        FileManager.deleteFile(TEST_FILE);
    }

    /**
     * Integration Test 1: Valid driver is stored correctly in TXT file.
     */
    @Test
    void testIntegration_validDriverStoredCorrectly() throws Exception {
        DriverRepository repo = new DriverRepository(TEST_FILE);
        Driver driver = new Driver("23@@bcXXAB", "John Smith", 5, "Heavy",
            "12|Main St|Melbourne|VIC|Australia", "15-06-1990");

        repo.addDriver(driver);

        Driver retrieved = repo.retrieveDriver("23@@bcXXAB");
        assertNotNull(retrieved);
        assertEquals("John Smith", retrieved.getName());
        assertEquals("Heavy", retrieved.getLicenseType());
    }

    /**
     * Integration Test 2: Invalid driver is rejected and not stored.
     */
    @Test
    void testIntegration_invalidDriverRejected() throws Exception {
        DriverRepository repo = new DriverRepository(TEST_FILE);
        Driver invalidDriver = new Driver("INVALID123", "Bad Driver", 2, "Light",
            "12|Main St|Melbourne|VIC|Australia", "15-06-1995");

        assertThrows(ValidationException.class, () -> {
            repo.addDriver(invalidDriver);
        });

        assertEquals(0, repo.countDrivers());
    }

    /**
     * Integration Test 3: Update is persisted correctly in TXT file.
     */
    @Test
    void testIntegration_updatePersistedCorrectly() throws Exception {
        DriverRepository repo = new DriverRepository(TEST_FILE);
        Driver driver = new Driver("23@@bcXXAB", "John Smith", 5, "Light",
            "12|Main St|Melbourne|VIC|Australia", "15-06-1990");
        repo.addDriver(driver);

        Driver updated = new Driver("23@@bcXXAB", "John Smith", 7, "Heavy",
            "99|New St|Sydney|NSW|Australia", "15-06-1990");
        repo.updateDriver("23@@bcXXAB", updated);

        // Reload from file to confirm persistence
        DriverRepository reloaded = new DriverRepository(TEST_FILE);
        Driver retrieved = reloaded.retrieveDriver("23@@bcXXAB");
        assertEquals("Heavy", retrieved.getLicenseType());
        assertEquals(7, retrieved.getExperienceYears());
    }

    /**
     * Integration Test 4: Record count is updated correctly after add.
     */
    @Test
    void testIntegration_recordCountUpdatedCorrectly() throws Exception {
        DriverRepository repo = new DriverRepository(TEST_FILE);

        assertEquals(0, repo.countDrivers());

        repo.addDriver(new Driver("23@@bcXXAB", "John Smith", 5, "Heavy",
            "12|Main St|Melbourne|VIC|Australia", "15-06-1990"));
        assertEquals(1, repo.countDrivers());

        repo.addDriver(new Driver("34@@bcXXAB", "Jane Doe", 3, "Light",
            "45|Park Ave|Sydney|NSW|Australia", "20-03-1995"));
        assertEquals(2, repo.countDrivers());
    }
}
