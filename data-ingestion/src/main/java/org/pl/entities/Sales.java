package org.pl.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
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

    public Sales() {
    }


    public int getSalesId() {
        return salesId;
    }

}
