package com.busdriver;

import java.io.*;
import java.util.*;

/**
 * DriverRepository handles Add, Retrieve, Update, Count for drivers.
 * Uses FileManager to read/write TXT file.
 */
public class DriverRepository {

    private String filePath;
    private List<Driver> drivers;

    /**
     * Constructor - loads existing drivers from TXT file.
     */
    public DriverRepository(String filePath) throws IOException {
        this.filePath = filePath;
        this.drivers = new ArrayList<>();
        loadFromFile();
    }

    /**
     * Adds a new driver after validating all conditions.
     * D1: driverID must be unique and valid format.
     * D2: address must follow correct format.
     * D3: birthdate must follow DD-MM-YYYY format.
     */
    public void addDriver(Driver driver) throws ValidationException, IOException {
        if (!Driver.isValidDriverID(driver.getDriverID())) {
            throw new ValidationException("D1: Invalid driver ID: " + driver.getDriverID());
        }
        if (retrieveDriver(driver.getDriverID()) != null) {
            throw new ValidationException("D1: Driver ID already exists: " + driver.getDriverID());
        }
        if (!Driver.isValidAddress(driver.getAddress())) {
            throw new ValidationException("D2: Invalid address format.");
        }
        if (!Driver.isValidBirthdate(driver.getBirthdate())) {
            throw new ValidationException("D3: Invalid birthdate format.");
        }
        drivers.add(driver);
        saveToFile();
    }

    /**
     * Retrieves a driver by ID.
     * Returns null if not found.
     */
    public Driver retrieveDriver(String driverID) {
        for (Driver d : drivers) {
            if (d.getDriverID().equals(driverID)) return d;
        }
        return null;
    }

    /**
     * Updates an existing driver.
     * D4: licenseType cannot change if experience > 10 years.
     * D5: driverID and name cannot be changed.
     */
    public void updateDriver(String driverID, Driver updated)
            throws ValidationException, IOException {
        Driver existing = retrieveDriver(driverID);
        if (existing == null) {
            throw new ValidationException("Driver not found: " + driverID);
        }
        // D5: driverID and name are immutable
        if (!updated.getDriverID().equals(existing.getDriverID())) {
            throw new ValidationException("D5: Driver ID cannot be changed.");
        }
        if (!updated.getName().equals(existing.getName())) {
            throw new ValidationException("D5: Driver name cannot be changed.");
        }
        // D4: cannot change licenseType if experience > 10 years
        if (existing.getExperienceYears() > 10 &&
            !updated.getLicenseType().equals(existing.getLicenseType())) {
            throw new ValidationException("D4: Cannot change license type for driver with more than 10 years experience.");
        }
        // D2: validate new address
        if (!Driver.isValidAddress(updated.getAddress())) {
            throw new ValidationException("D2: Invalid address format.");
        }
        // D3: validate new birthdate
        if (!Driver.isValidBirthdate(updated.getBirthdate())) {
            throw new ValidationException("D3: Invalid birthdate format.");
        }
        existing.setExperienceYears(updated.getExperienceYears());
        existing.setLicenseType(updated.getLicenseType());
        existing.setAddress(updated.getAddress());
        existing.setBirthdate(updated.getBirthdate());
        saveToFile();
    }

    /**
     * Returns total number of stored drivers.
     */
    public int countDrivers() {
        return drivers.size();
    }

    /**
     * Loads drivers from TXT file into memory.
     * Format: driverID|name|experienceYears|licenseType|address|birthdate
     */
    private void loadFromFile() throws IOException {
        drivers.clear();
        List<String> lines = FileManager.readAll(filePath);
        for (String line : lines) {
            String[] parts = line.split("\\|", 6);
            if (parts.length < 6) continue;
            try {
                String driverID = parts[0].trim();
                String name = parts[1].trim();
                int exp = Integer.parseInt(parts[2].trim());
                String license = parts[3].trim();
                String address = parts[4].trim();
                String birthdate = parts[5].trim();
                drivers.add(new Driver(driverID, name, exp, license, address, birthdate));
            } catch (NumberFormatException e) {
                // skip malformed lines
            }
        }
    }

    /**
     * Saves all drivers to TXT file.
     * Format: driverID|name|experienceYears|licenseType|address|birthdate
     */
    private void saveToFile() throws IOException {
        List<String> lines = new ArrayList<>();
        for (Driver d : drivers) {
            lines.add(d.getDriverID() + "|" + d.getName() + "|" +
                      d.getExperienceYears() + "|" + d.getLicenseType() + "|" +
                      d.getAddress() + "|" + d.getBirthdate());
        }
        FileManager.writeAll(filePath, "Driver Data - Format: driverID|name|experienceYears|licenseType|address|birthdate", lines);
    }
}
