package com.busdriver;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * Represents a bus driver in the Intelligent Bus Driver Guidance System.
 */
public class Driver {
    private String driverID;
    private String name;
    private int experienceYears;
    private String licenseType;
    private String address;
    private String birthdate;

    public Driver(String driverID, String name, int experienceYears, String licenseType, String address, String birthdate) {
        this.driverID = driverID;
        this.name = name;
        this.experienceYears = experienceYears;
        this.licenseType = licenseType;
        this.address = address;
        this.birthdate = birthdate;
    }

    public String getDriverID() {
        return driverID;
    }

    public String getName() {
        return name;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public String getAddress() {
        return address;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public int getAge() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate dob = LocalDate.parse(birthdate, formatter);
        return Period.between(dob, LocalDate.now()).getYears();
    }

    public static boolean isValidDriverID(String driverID) {
        if (driverID == null || driverID.length() != 10) {
            return false;
        }

        if (!Character.isDigit(driverID.charAt(0)) || !Character.isDigit(driverID.charAt(1))) {
            return false;
        }

        int firstDigit = Character.getNumericValue(driverID.charAt(0));
        int secondDigit = Character.getNumericValue(driverID.charAt(1));

        if (firstDigit < 2 || firstDigit > 9 || secondDigit < 2 || secondDigit > 9) {
            return false;
        }

        int specialCount = 0;
        for (int i = 2; i <= 7; i++) {
            char c = driverID.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                specialCount++;
            }
        }

        if (specialCount < 2) {
            return false;
        }

        return Character.isUpperCase(driverID.charAt(8)) && Character.isUpperCase(driverID.charAt(9));
    }

    public static boolean isValidAddress(String address) {
        if (address == null) {
            return false;
        }

        String[] parts = address.split("\\|");
        return parts.length == 5
                && !parts[0].isBlank()
                && !parts[1].isBlank()
                && !parts[2].isBlank()
                && !parts[3].isBlank()
                && !parts[4].isBlank();
    }

    public static boolean isValidBirthdate(String birthdate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate.parse(birthdate, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}