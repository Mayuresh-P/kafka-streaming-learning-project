package org.pl.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Sales {

    @JsonProperty
    private int salesId;

    @JsonProperty
    private Date timestamp;

    @JsonProperty
    private int productId;

    @JsonProperty
    private int customerId;

    @JsonProperty
    private int quantity;

    @JsonProperty
    private int price;

    @JsonProperty
    private String productCategory;

    public Sales(int salesId, Date timestamp, int productId, int customerId, String productCategory, int quantity, int price) {
        this.salesId = salesId;
        this.timestamp = timestamp;
        this.productId = productId;
        this.customerId = customerId;
        this.quantity = quantity;
        this.productCategory = productCategory;
        this.price = price;
    }

    public Sales() {

    }

    @Override
    public String toString() {
        return "Sales{" +
                "salesId=" + salesId +
                ", timestamp=" + timestamp +
                ", productId=" + productId +
                ", customerId=" + customerId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", productCategory='" + productCategory + '\'' +
                '}';
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

    public void setTimestamp (Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
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
