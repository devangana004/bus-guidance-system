package com.busdriver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Bus operations.
 * Written by: Pavi
 */
public class BusIntegrationTest {

    private static final String TEST_FILE = "data/integration_buses.txt";

    @BeforeEach
    void cleanUp() {
        FileManager.deleteFile(TEST_FILE);
    }

    @Test
    void testIntegration_validBusStoredCorrectly() throws Exception {
        BusRepository repo = new BusRepository(TEST_FILE);
        Bus bus = new Bus("12345678", 40, 80.0, "Diesel");
        repo.addBus(bus);
        Bus retrieved = repo.retrieveBus("12345678");
        assertNotNull(retrieved);
        assertEquals(40, retrieved.getCapacity());
        assertEquals("Diesel", retrieved.getFuelType());
    }

    @Test
    void testIntegration_invalidBusRejected() throws Exception {
        BusRepository repo = new BusRepository(TEST_FILE);
        Bus invalidBus = new Bus("INVALID", 40, 80.0, "Diesel");
        assertThrows(ValidationException.class, () -> {
            repo.addBus(invalidBus);
        });
        assertEquals(0, repo.countBuses());
    }

    @Test
    void testIntegration_updatePersistedCorrectly() throws Exception {
        BusRepository repo = new BusRepository(TEST_FILE);
        Bus bus = new Bus("12345678", 40, 80.0, "Diesel");
        repo.addBus(bus);
        Bus updated = new Bus("12345678", 30, 60.0, "Hybrid");
        repo.updateBus(updated);
        BusRepository reloaded = new BusRepository(TEST_FILE);
        Bus retrieved = reloaded.retrieveBus("12345678");
        assertEquals(30, retrieved.getCapacity());
        assertEquals("Hybrid", retrieved.getFuelType());
    }

    @Test
    void testIntegration_recordCountUpdatedCorrectly() throws Exception {
        BusRepository repo = new BusRepository(TEST_FILE);
        assertEquals(0, repo.countBuses());
        repo.addBus(new Bus("12345678", 40, 80.0, "Diesel"));
        assertEquals(1, repo.countBuses());
        repo.addBus(new Bus("87654321", 30, 60.0, "Hybrid"));
        assertEquals(2, repo.countBuses());
    }
}
