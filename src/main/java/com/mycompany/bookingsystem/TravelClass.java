package com.mycompany.bookingsystem;

import java.math.BigDecimal;

public class TravelClass {

    private int classId;
    private String className;
    private BigDecimal priceMultiplier;

    public TravelClass() {
    }

    public TravelClass(String className, BigDecimal priceMultiplier) {
        this.className = className;
        this.priceMultiplier = priceMultiplier;
    }

    // Getters and setters
    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public BigDecimal getPriceMultiplier() {
        return priceMultiplier;
    }

    public void setPriceMultiplier(BigDecimal priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }

    @Override
    public String toString() {
        return className;
    }
}
