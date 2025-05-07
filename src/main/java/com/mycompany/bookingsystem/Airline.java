package com.mycompany.bookingsystem;

public class Airline {

    private int airlineId;
    private String airlineCode;
    private String airlineName;
    private boolean isActive;

    public Airline() {
    }

    public Airline(String airlineCode, String airlineName) {
        this.airlineCode = airlineCode;
        this.airlineName = airlineName;
        this.isActive = true;
    }

    // Getters and setters
    public int getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(int airlineId) {
        this.airlineId = airlineId;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return airlineCode + " - " + airlineName;
    }
}
