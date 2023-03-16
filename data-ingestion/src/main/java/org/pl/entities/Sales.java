package org.pl.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class Sales {

    @JsonProperty
    private long salesId;

    @JsonProperty
    private Instant timestamp;

    @JsonProperty
    private int productId;

    @JsonProperty
    private long customerId;

    @JsonProperty
    private long quantity;

    @JsonProperty
    private long price;

    @JsonProperty
    private String productCategory;

    public Sales(int salesId, Instant timestamp, int productId, int customerId, String productCategory, long quantity, long price) {
        this.salesId = salesId;
        this.timestamp = timestamp;
        this.productId = productId;
        this.customerId = customerId;
        this.quantity = quantity;
        this.productCategory = productCategory;
        this.price = price;
    }

}
