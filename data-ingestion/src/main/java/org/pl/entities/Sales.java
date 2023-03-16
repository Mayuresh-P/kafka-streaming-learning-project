package org.pl.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class Sales {

    @JsonProperty
    private int salesId;

    @JsonProperty
    private Instant timestamp;

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

    public Sales(int salesId, Instant timestamp, int productId, int customerId, String productCategory, int quantity, int price) {
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

}
