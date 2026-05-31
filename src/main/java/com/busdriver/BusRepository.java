package com.busdriver;

import java.io.*;
import java.util.*;

/**
 * Manages all bus records.
 * Enforces bus validation rules B1-B5.
 * Saves and loads bus data from a TXT file.
 */
public class BusRepository {

    private String filePath;
    private List<BusUnitTest> buses;

    /**
     * Constructor - loads existing buses from TXT file.
     * @param filePath path to the TXT file
     */
    public BusRepository(String filePath) throws IOException {
        this.filePath = filePath;
        this.buses = new ArrayList<>();
        loadFromFile();
    }

    /**
     * Adds a new bus after validating B1.
     * Saves to TXT file after adding.
     * B1: busID must be unique and exactly 8 digits.
     */
    public void addBus(BusUnitTest bus) 
            throws ValidationException, IOException {
        validateBusID(bus.getBusID());
        if (retrieveBus(bus.getBusID()) != null) {
            throw new ValidationException(
                "B1: Bus ID already exists: " + bus.getBusID());
        }
        buses.add(bus);
        saveToFile();
    }

    /**
     * Retrieves a bus by its ID.
     * Returns null if not found.
     */
    public BusUnitTest retrieveBus(String busID) {
        for (BusUnitTest bus : buses) {
            if (bus.getBusID().equals(busID)) {
                return bus;
            }
        }
        return null;
    }

    /**
     * Updates an existing bus.
     * Enforces B2: capacity cannot increase.
     * Saves to TXT file after updating.
     */
    public void updateBus(BusUnitTest updatedBus) 
            throws ValidationException, IOException {
        BusUnitTest existing = retrieveBus(updatedBus.getBusID());
        if (existing == null) {
            throw new ValidationException(
                "Bus not found: " + updatedBus.getBusID());
        }
        // B2: capacity can only decrease, never increase
        if (updatedBus.getCapacity() > existing.getCapacity()) {
            throw new ValidationException(
                "B2: Bus capacity cannot increase during update.");
        }
        existing.setCapacity(updatedBus.getCapacity());
        existing.setFuelLevel(updatedBus.getFuelLevel());
        existing.setFuelType(updatedBus.getFuelType());
        saveToFile();
    }

    /**
     * Returns total number of stored buses.
     */
    public int countBuses() {
        return buses.size();
    }

    /**
     * Checks if a driver can drive a specific bus.
     * Enforces B3, B4, B5.
     * B3: drivers older than 50 cannot drive buses 
     *     with capacity >= 50.
     * B4: Electricity buses need driver with 
     *     >= 5 years experience.
     * B5: Electricity and Hybrid buses need Heavy 
     *     or PublicTransport licence.
     */
    public void checkDriverBusCompatibility(
            Driver driver, BusUnitTest bus) throws ValidationException {

        // B3: check driver age vs bus capacity
        int age = calculateAge(driver.getBirthdate());
        if (age > 50 && bus.getCapacity() >= 50) {
            throw new ValidationException(
                "B3: Driver over 50 cannot drive bus " +
                "with capacity >= 50.");
        }

        // B4: check experience for Electricity buses
        if (bus.getFuelType().equalsIgnoreCase("Electricity") 
                && driver.getExperienceYears() < 5) {
            throw new ValidationException(
                "B4: Electricity bus needs driver with " +
                "at least 5 years experience.");
        }

        // B5: check licence for Electricity or Hybrid buses
        if (bus.getFuelType().equalsIgnoreCase("Electricity") 
            || bus.getFuelType().equalsIgnoreCase("Hybrid")) {
            String licence = driver.getLicenseType();
            if (!licence.equalsIgnoreCase("Heavy") && 
                !licence.equalsIgnoreCase("PublicTransport")) {
                throw new ValidationException(
                    "B5: Electricity/Hybrid buses need Heavy " +
                    "or PublicTransport licence.");
            }
        }
    }

    /**
     * Validates B1: busID must be exactly 8 digits only.
     */
    private void validateBusID(String busID) 
            throws ValidationException {
        if (busID == null || !busID.matches("\\d{8}")) {
            throw new ValidationException(
                "B1: Bus ID must be exactly 8 digits.");
        }
    }

    /**
     * Calculates age from birthdate string DD-MM-YYYY.
     */
    private int calculateAge(String birthdate) {
        try {
            String[] parts = birthdate.split("-");
            int day   = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year  = Integer.parseInt(parts[2]);
            Calendar dob = Calendar.getInstance();
            dob.set(year, month - 1, day);
            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) 
                    - dob.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) 
                    < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            return age;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Loads buses from TXT file into memory.
     * Each line format: busID|capacity|fuelLevel|fuelType
     */
    private void loadFromFile() throws IOException {
        buses.clear();
        List<String> lines = FileManager.readAll(filePath);
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length < 4) continue;
            try {
                String busID    = parts[0].trim();
                int capacity    = Integer.parseInt(parts[1].trim());
                double fuelLevel = Double.parseDouble(parts[2].trim());
                String fuelType = parts[3].trim();
                buses.add(new BusUnitTest(busID, capacity, 
                                  fuelLevel, fuelType));
            } catch (NumberFormatException e) {
                // skip malformed lines
            }
        }
    }

    /**
     * Saves all buses to TXT file.
     * Format: busID|capacity|fuelLevel|fuelType
     */
    private void saveToFile() throws IOException {
        FileManager.writeAll(filePath,
            "Bus Data - Format: busID|capacity|fuelLevel|fuelType",
            buses);
    }
}