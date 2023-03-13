package org.pl.entities;

import java.time.Instant;

public class Order {

    private long orderId;

    private Instant timestamp;

    private int productId;

    private long customerId;

    private long quantity;

    private long price;

    private String productCategory;

    public Order(int orderId, Instant timestamp, int productId, int customerId, String productCategory, long quantity, long price) {
        this.orderId = orderId;
        this.timestamp = timestamp;
        this.productId = productId;
        this.customerId = customerId;
        this.quantity = quantity;
        this.productCategory = productCategory;
        this.price = price;
    }

}
