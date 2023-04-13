package org.pl.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TotalSalesByCategory {

    @JsonProperty("category")
    private String category;

    @JsonProperty("total_sales")
    private int totalSales;

    public TotalSalesByCategory() {
        category = "";
        totalSales = 0;
    }

    public TotalSalesByCategory(String category, int totalSales) {
        this.category = category;
        this.totalSales = totalSales;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }


}
