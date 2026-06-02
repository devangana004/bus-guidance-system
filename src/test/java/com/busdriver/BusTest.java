package com.busdriver;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

public class BusTest {

    private BusRepository repo;
    private final String testFile = "data/test-buses.txt";

    @BeforeEach
    void setup() throws Exception {
        FileManager.deleteFile(testFile);
        repo = new BusRepository(testFile);
    }

    // B1: Bus ID must be unique and exactly 8 digits

    @Test
    void validBusIDShouldBeAccepted() throws Exception {
        Bus bus = new Bus("12345678", 40, 80.0, "Diesel");
        repo.addBus(bus);
        assertEquals(1, repo.countBuses());
    }

    @Test
    void shortBusIDShouldBeRejected() {
        Bus bus = new Bus("12345", 40, 80.0, "Diesel");
        assertThrows(ValidationException.class, () -> repo.addBus(bus));
    }

    @Test
    void nonDigitBusIDShouldBeRejected() {
        Bus bus = new Bus("1234ABCD", 40, 80.0, "Diesel");
        assertThrows(ValidationException.class, () -> repo.addBus(bus));
    }

    @Test
    void duplicateBusIDShouldBeRejected() throws Exception {
        Bus bus1 = new Bus("12345678", 40, 80.0, "Diesel");
        Bus bus2 = new Bus("12345678", 30, 70.0, "Hybrid");

        repo.addBus(bus1);

        assertThrows(ValidationException.class, () -> repo.addBus(bus2));
    }

    // B2: Capacity cannot increase during update

    @Test
    void capacityDecreaseShouldBeAllowed() throws Exception {
        Bus bus = new Bus("11112222", 50, 80.0, "Diesel");
        repo.addBus(bus);

        Bus updated = new Bus("11112222", 40, 70.0, "Diesel");
        repo.updateBus(updated);

        assertEquals(40, repo.retrieveBus("11112222").getCapacity());
    }

    @Test
    void sameCapacityShouldBeAllowed() throws Exception {
        Bus bus = new Bus("22223333", 50, 80.0, "Diesel");
        repo.addBus(bus);

        Bus updated = new Bus("22223333", 50, 60.0, "Diesel");
        repo.updateBus(updated);

        assertEquals(50, repo.retrieveBus("22223333").getCapacity());
    }

    @Test
    void capacityIncreaseShouldBeRejected() throws Exception {
        Bus bus = new Bus("33334444", 40, 80.0, "Diesel");
        repo.addBus(bus);

        Bus updated = new Bus("33334444", 60, 70.0, "Diesel");

        assertThrows(ValidationException.class, () -> repo.updateBus(updated));
    }

    // B3: Drivers older than 50 cannot drive buses with capacity >= 50

    @Test
    void driverOver50LargeBusShouldBeRejected() {
        Driver driver = new Driver("23@@ABCDXY", "John", 20, "Heavy",
                "12|Main Street|Melbourne|VIC|Australia", "01-01-1970");

        Bus bus = new Bus("44445555", 50, 80.0, "Diesel");

        assertThrows(ValidationException.class,
                () -> repo.checkDriverBusCompatibility(driver, bus));
    }

    @Test
    void driverOver50SmallBusShouldBeAllowed() {
        Driver driver = new Driver("23@@ABCDXY", "John", 20, "Heavy",
                "12|Main Street|Melbourne|VIC|Australia", "01-01-1970");

        Bus bus = new Bus("55556666", 40, 80.0, "Diesel");

        assertDoesNotThrow(() -> repo.checkDriverBusCompatibility(driver, bus));
    }

    @Test
    void driverUnder50LargeBusShouldBeAllowed() {
        Driver driver = new Driver("23@@ABCDXY", "John", 10, "Heavy",
                "12|Main Street|Melbourne|VIC|Australia", "01-01-1990");

        Bus bus = new Bus("66667777", 50, 80.0, "Diesel");

        assertDoesNotThrow(() -> repo.checkDriverBusCompatibility(driver, bus));
    }

    // B4: Electricity buses need driver with at least 5 years experience

    @Test
    void electricBusWithExperiencedDriverShouldBeAllowed() {
        Driver driver = new Driver("23@@ABCDXY", "John", 5, "Heavy",
                "12|Main Street|Melbourne|VIC|Australia", "01-01-1990");

        Bus bus = new Bus("77778888", 40, 80.0, "Electricity");

        assertDoesNotThrow(() -> repo.checkDriverBusCompatibility(driver, bus));
    }

    @Test
    void electricBusWithLowExperienceDriverShouldBeRejected() {
        Driver driver = new Driver("23@@ABCDXY", "John", 4, "Heavy",
                "12|Main Street|Melbourne|VIC|Australia", "01-01-1990");

        Bus bus = new Bus("88889999", 40, 80.0, "Electricity");

        assertThrows(ValidationException.class,
                () -> repo.checkDriverBusCompatibility(driver, bus));
    }

    @Test
    void dieselBusWithLowExperienceDriverShouldBeAllowed() {
        Driver driver = new Driver("23@@ABCDXY", "John", 2, "Light",
                "12|Main Street|Melbourne|VIC|Australia", "01-01-1990");

        Bus bus = new Bus("99990000", 40, 80.0, "Diesel");

        assertDoesNotThrow(() -> repo.checkDriverBusCompatibility(driver, bus));
    }

    // B5: Electric and Hybrid buses need Heavy or PublicTransport licence

    @Test
    void hybridBusWithHeavyLicenceShouldBeAllowed() {
        Driver driver = new Driver("23@@ABCDXY", "John", 8, "Heavy",
                "12|Main Street|Melbourne|VIC|Australia", "01-01-1990");

        Bus bus = new Bus("12121212", 40, 80.0, "Hybrid");

        assertDoesNotThrow(() -> repo.checkDriverBusCompatibility(driver, bus));
    }

    @Test
    void electricBusWithPublicTransportLicenceShouldBeAllowed() {
        Driver driver = new Driver("23@@ABCDXY", "John", 8, "PublicTransport",
                "12|Main Street|Melbourne|VIC|Australia", "01-01-1990");

        Bus bus = new Bus("34343434", 40, 80.0, "Electricity");

        assertDoesNotThrow(() -> repo.checkDriverBusCompatibility(driver, bus));
    }

    @Test
    void hybridBusWithLightLicenceShouldBeRejected() {
        Driver driver = new Driver("23@@ABCDXY", "John", 8, "Light",
                "12|Main Street|Melbourne|VIC|Australia", "01-01-1990");

        Bus bus = new Bus("56565656", 40, 80.0, "Hybrid");

        assertThrows(ValidationException.class,
                () -> repo.checkDriverBusCompatibility(driver, bus));
    }
}