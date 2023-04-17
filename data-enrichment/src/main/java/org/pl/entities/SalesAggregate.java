package org.pl.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SalesAggregate {
    @JsonProperty("category")
    private int total_sale_count;

    @JsonProperty("total_sales")
    private int totalSales;

    @JsonProperty("average_sales")
    private float avgSales;

    public SalesAggregate() {
    }

    public SalesAggregate(int total_sale_count, int totalSales, float avgSales) {
        this.total_sale_count = total_sale_count;
        this.totalSales = totalSales;
        this.avgSales = avgSales;
    }

    public SalesAggregate withTotal_sale_count(int total_sale_count){
        this.total_sale_count = total_sale_count;
        return this;
    }
    public SalesAggregate withTotalSales(int totalSales){
        this.totalSales = totalSales;
        return this;
    }
    public SalesAggregate withAvgSales(int total_sale_count){
        this.avgSales = avgSales;
        return this;
    }

    public void setTotal_sale_count(int total_sale_count) {
        this.total_sale_count = total_sale_count;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }

    public void setAvgSales(float avgSales) {
        this.avgSales = avgSales;
    }

    public int getTotal_sale_count() {
        return total_sale_count;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public float getAvgSales() {
        return avgSales;
    }
}
