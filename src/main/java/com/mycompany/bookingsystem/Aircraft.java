package com.mycompany.bookingsystem;

public class Aircraft {

    private int aircraftId;
    private String aircraftCode;
    private String model;
    private int totalSeats;
    private int airlineId;

    public Aircraft() {
    }

    public Aircraft(String aircraftCode, String model, int totalSeats, int airlineId) {
        this.aircraftCode = aircraftCode;
        this.model = model;
        this.totalSeats = totalSeats;
        this.airlineId = airlineId;
    }

    // Getters and setters
    public int getAircraftId() {
        return aircraftId;
    }

    public void setAircraftId(int aircraftId) {
        this.aircraftId = aircraftId;
    }

    public String getAircraftCode() {
        return aircraftCode;
    }

    public void setAircraftCode(String aircraftCode) {
        this.aircraftCode = aircraftCode;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(int airlineId) {
        this.airlineId = airlineId;
    }

    @Override
    public String toString() {
        return model + " (" + aircraftCode + ")";
    }
}
