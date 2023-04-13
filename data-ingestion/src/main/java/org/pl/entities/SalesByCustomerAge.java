package org.pl.entities;

public class SalesByCustomerAge {
    private String ageSegment;
    private int totalSales;
    private int averageSales;
    private int salesCount;

    public SalesByCustomerAge() {
    }

    public SalesByCustomerAge(String ageSegment, int totalSales, int averageSales, int salesCount) {
        this.ageSegment = ageSegment;
        this.totalSales = totalSales;
        this.averageSales = averageSales;
        this.salesCount = salesCount;
    }

    public String getAgeSegment() {
        return ageSegment;
    }

    public void setAgeSegment(String ageSegment) {
        this.ageSegment = ageSegment;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }

    public int getAverageSales() {
        return averageSales;
    }

    public void setAverageSales(int averageSales) {
        this.averageSales = averageSales;
    }

    public int getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(int salesCount) {
        this.salesCount = salesCount;
    }
}
