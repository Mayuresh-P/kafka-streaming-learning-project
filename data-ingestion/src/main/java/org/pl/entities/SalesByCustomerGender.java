package org.pl.entities;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class SalesByCustomerGender {

    private String customerGender;
    private int totalSales;
    private int averageSales;
    private int salesCount;



    private HashMap<String, Integer> categoryWiseSales;

    public SalesByCustomerGender aggregate(String customerGender, int currentSales, String category){
        this.customerGender = customerGender;
        this.totalSales += totalSales;
        this.salesCount += 1;
        this.averageSales = (this.totalSales+currentSales)/this.salesCount;
        if(this.categoryWiseSales.isEmpty()){
            this.categoryWiseSales.put(category, currentSales);
        }
        else if(this.categoryWiseSales.containsKey(category)){
            int totalSales = this.categoryWiseSales.get(category);
            this.categoryWiseSales.put(category, totalSales+currentSales);
        } else {
            this.categoryWiseSales.put(category, currentSales);
        }
        return  this;
    }

    public HashMap<String, Integer> getCategoryWiseSales() {
        return categoryWiseSales;
    }

    public void setCategoryWiseSales(HashMap<String, Integer> categoryWiseSales) {
        this.categoryWiseSales = categoryWiseSales;
    }
    public SalesByCustomerGender() {
        this.totalSales = 0;
        this.categoryWiseSales = new HashMap<>();
        this.customerGender = "";
        this.salesCount = 0;
        this.averageSales = 0;
    }

    public SalesByCustomerGender(String customerGender, int totalSales, int averageSales, int salesCount, HashMap<String, Integer> categoryWiseSales) {
        this.customerGender = customerGender;
        this.totalSales = totalSales;
        this.averageSales = averageSales;
        this.salesCount = salesCount;
        this.categoryWiseSales = categoryWiseSales;
    }

    public String getCustomerGender() {
        return customerGender;
    }

    public void setCustomerGender(String customerGender) {
        this.customerGender = customerGender;
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

    public void addCategoryWiseSales(String category, int sales){
        if(this.categoryWiseSales.isEmpty()){
            this.categoryWiseSales.put(category, sales);
        }
        else if(this.categoryWiseSales.containsKey(category)){
            int totalSales = this.categoryWiseSales.get(category);
            this.categoryWiseSales.put(category, totalSales+sales);
        } else {
            this.categoryWiseSales.put(category, sales);
        }

    }
}
