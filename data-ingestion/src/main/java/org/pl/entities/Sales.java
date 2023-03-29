package org.pl.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.opencsv.bean.CsvBindByName;

@JsonPropertyOrder({"orderId","productId","productCategory","customerId","quantity","price"})
public class Sales {

    @JsonProperty("orderId")
    @CsvBindByName(column = "orderId")
    private int orderId;

    //private Instant timestamp;
    @JsonProperty("productId")
    @CsvBindByName(column = "productId")
    private int productId;
    @JsonProperty("productCategory")
    @CsvBindByName(column = "productCategory")
    private String productCategory;
    @JsonProperty("customerId")
    @CsvBindByName(column = "customerId")
    private int customerId;

    @JsonProperty("quantity")
    @CsvBindByName(column = "quantity")
    private long quantity;

    @JsonProperty("price")
    @CsvBindByName(column = "price")
    private long price;

//    public Sales(int orderId/*, Instant timestamp*/, int productId,String productCategory, int customerId, long quantity, long price) {
//        this.orderId = orderId;
//        //this.timestamp = timestamp;
//        this.productId = productId;
//        this.customerId = customerId;
//        this.quantity = quantity;
//        this.price = price;
//    }

    @Override
    public java.lang.String toString() {
        return "Sales{" +
                "orderId=" + orderId +/*
                ", timestamp=" + timestamp +*/
                ", productId=" + productId +
                ", productCategory='" + productCategory + '\'' +
                ", customerId=" + customerId +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }

    @JsonProperty("orderId")
    public int getOrderId() {
        return orderId;
    }
}
