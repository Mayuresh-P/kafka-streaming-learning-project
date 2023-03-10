package org.pl.entities;

import java.time.Instant;

public class Order {

    private int orderId;

    private Instant timestamp;

    private int productId;

    private int customerId;

    private long quantity;

    private long price;

    public Order(int orderId, Instant timestamp, int productId, int customerId, long quantity, long price) {
        this.orderId = orderId;
        this.timestamp = timestamp;
        this.productId = productId;
        this.customerId = customerId;
        this.quantity = quantity;
        this.price = price;
    }

}
