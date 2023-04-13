package org.pl.entities;

public class AllSalesStats {
    private int salesCount;
    private int totalSales;
    private int averageSales;

    public AllSalesStats() {
    }

    public AllSalesStats(int salesCount, int totalSales, int averageSales) {
        this.salesCount = salesCount;
        this.totalSales = totalSales;
        this.averageSales = averageSales;
    }

    public int getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(int salesCount) {
        this.salesCount = salesCount;
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
}
