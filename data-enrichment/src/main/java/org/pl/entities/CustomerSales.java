package org.pl.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class CustomerSales {
    @JsonProperty
    private int customerId;

    @JsonProperty
    private String gender;

    @JsonProperty
    private int age;

    @JsonProperty
    private String name;

    @JsonProperty
    private int salesId;

    @JsonProperty
    private Date timestamp;

    @JsonProperty
    private int productId;

    @JsonProperty
    private int quantity;

    @JsonProperty
    private int price;

    @JsonProperty
    private String productCategory;

    public CustomerSales(int customerId, String gender, int age, String name, int salesId, Date timestamp, int productId, int quantity, int price, String productCategory) {
        this.customerId = customerId;
        this.gender = gender;
        this.age = age;
        this.name = name;
        this.salesId = salesId;
        this.timestamp = timestamp;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.productCategory = productCategory;
    }

    public CustomerSales() {
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSalesId() {
        return salesId;
    }

    public void setSalesId(int salesId) {
        this.salesId = salesId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }
}
