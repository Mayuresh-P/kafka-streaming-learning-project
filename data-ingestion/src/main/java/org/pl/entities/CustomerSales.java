package org.pl.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
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

}
