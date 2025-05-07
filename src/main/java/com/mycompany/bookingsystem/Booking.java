package com.mycompany.bookingsystem;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Booking {

    private int bookingId;
    private String bookingReference;
    private int passengerId;
    private int flightId;
    private int classId;
    private String seatNumber;
    private Timestamp bookingDate;
    private String bookingStatus;
    private BigDecimal totalAmount;

    // Transient properties for display
    private String passengerName;
    private String flightNumber;
    private String className;

    public Booking() {
    }

    public Booking(int passengerId, int flightId, int classId, BigDecimal totalAmount) {
        this.passengerId = passengerId;
        this.flightId = flightId;
        this.classId = classId;
        this.totalAmount = totalAmount;
        this.bookingStatus = "Reserved";
        // Booking reference will be generated when saving to database
    }

    // Getters and setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Timestamp getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Timestamp bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return bookingReference + " | " + passengerName + " | "
                + flightNumber + " | " + className + " | " + bookingStatus;
    }
}
