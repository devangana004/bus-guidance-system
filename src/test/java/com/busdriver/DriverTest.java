package com.busdriver;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Driver conditions D1-D5.
 * Written by: Devangana
 */
public class DriverTest {

    // ========== D1: Driver ID Tests ==========

    @Test
    void testD1_validDriverID() {
        assertTrue(Driver.isValidDriverID("23@@bcXXAB"));
    }

    @Test
    void testD1_invalidDriverID_tooShort() {
        assertFalse(Driver.isValidDriverID("23@@bcXA"));
    }

    @Test
    void testD1_invalidDriverID_firstDigitBelowTwo() {
        assertFalse(Driver.isValidDriverID("13@@bcXXAB"));
    }

    @Test
    void testD1_invalidDriverID_noSpecialChars() {
        assertFalse(Driver.isValidDriverID("23abcdXXAB"));
    }

    @Test
    void testD1_invalidDriverID_lastNotUppercase() {
        assertFalse(Driver.isValidDriverID("23@@bcXXab"));
    }

    // ========== D2: Address Format Tests ==========

    @Test
    void testD2_validAddress() {
        assertTrue(Driver.isValidAddress("12|Main St|Melbourne|VIC|Australia"));
    }

    @Test
    void testD2_invalidAddress_missingParts() {
        assertFalse(Driver.isValidAddress("12|Main St|Melbourne"));
    }

    @Test
    void testD2_invalidAddress_null() {
        assertFalse(Driver.isValidAddress(null));
    }

    // ========== D3: Birthdate Format Tests ==========

    @Test
    void testD3_validBirthdate() {
        assertTrue(Driver.isValidBirthdate("15-06-1990"));
    }

    @Test
    void testD3_invalidBirthdate_wrongFormat() {
        assertFalse(Driver.isValidBirthdate("1990-06-15"));
    }

    @Test
    void testD3_invalidBirthdate_null() {
        assertFalse(Driver.isValidBirthdate(null));
    }

    // ========== D4: License Update Restriction Tests ==========

    @Test
    void testD4_cannotChangeLicenseOver10Years() throws Exception {
        Driver driver = new Driver("23@@bcXXAB", "John", 11, "Heavy",
            "12|Main St|Melbourne|VIC|Australia", "15-06-1980");
        DriverRepository repo = new DriverRepository("data/test_drivers_d4.txt");
        repo.addDriver(driver);
        Driver updated = new Driver("23@@bcXXAB", "John", 11, "Light",
            "12|Main St|Melbourne|VIC|Australia", "15-06-1980");
        assertThrows(ValidationException.class, () -> {
            repo.updateDriver("23@@bcXXAB", updated);
        });
    }

    @Test
    void testD4_canChangeLicenseUnder10Years() throws Exception {
        Driver driver = new Driver("34@@bcXXAB", "Jane", 5, "Light",
            "12|Main St|Melbourne|VIC|Australia", "15-06-1990");
        DriverRepository repo = new DriverRepository("data/test_drivers_d4b.txt");
        repo.addDriver(driver);
        Driver updated = new Driver("34@@bcXXAB", "Jane", 5, "Heavy",
            "12|Main St|Melbourne|VIC|Australia", "15-06-1990");
        assertDoesNotThrow(() -> repo.updateDriver("34@@bcXXAB", updated));
    }

    @Test
    void testD4_exactly10YearsCanChange() throws Exception {
        Driver driver = new Driver("44@@bcXXAB", "Sam", 10, "Light",
            "12|Main St|Melbourne|VIC|Australia", "15-06-1985");
        DriverRepository repo = new DriverRepository("data/test_drivers_d4c.txt");
        repo.addDriver(driver);
        Driver updated = new Driver("44@@bcXXAB", "Sam", 10, "Heavy",
            "12|Main St|Melbourne|VIC|Australia", "15-06-1985");
        assertDoesNotThrow(() -> repo.updateDriver("44@@bcXXAB", updated));
    }

    // ========== D5: Immutable Fields Tests ==========

    @Test
    void testD5_cannotChangeDriverID() throws Exception {
        Driver driver = new Driver("45@@bcXXAB", "Bob", 3, "Light",
            "12|Main St|Melbourne|VIC|Australia", "15-06-1995");
        DriverRepository repo = new DriverRepository("data/test_drivers_d5.txt");
        repo.addDriver(driver);
        Driver updated = new Driver("99@@bcXXAB", "Bob", 3, "Light",
            "12|Main St|Melbourne|VIC|Australia", "15-06-1995");
        assertThrows(ValidationException.class, () -> {
            repo.updateDriver("45@@bcXXAB", updated);
        });
    }

    @Test
    void testD5_cannotChangeName() throws Exception {
        Driver driver = new Driver("56@@bcXXAB", "Alice", 3, "Light",
            "12|Main St|Melbourne|VIC|Australia", "15-06-1995");
        DriverRepository repo = new DriverRepository("data/test_drivers_d5b.txt");
        repo.addDriver(driver);
        Driver updated = new Driver("56@@bcXXAB", "NewName", 3, "Light",
            "12|Main St|Melbourne|VIC|Australia", "15-06-1995");
        assertThrows(ValidationException.class, () -> {
            repo.updateDriver("56@@bcXXAB", updated);
        });
    }

    @Test
    void testD5_canUpdateOtherFields() throws Exception {
        Driver driver = new Driver("67@@bcXXAB", "Tom", 3, "Light",
            "12|Main St|Melbourne|VIC|Australia", "15-06-1995");
        DriverRepository repo = new DriverRepository("data/test_drivers_d5c.txt");
        repo.addDriver(driver);
        Driver updated = new Driver("67@@bcXXAB", "Tom", 5, "Heavy",
            "99|New St|Sydney|NSW|Australia", "15-06-1995");
        assertDoesNotThrow(() -> repo.updateDriver("67@@bcXXAB", updated));
    }
}
