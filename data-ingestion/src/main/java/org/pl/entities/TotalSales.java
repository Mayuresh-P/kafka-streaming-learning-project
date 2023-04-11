package org.pl.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.kafka.common.serialization.Serdes;

import javax.inject.Inject;

@Data
public class TotalSales {

    @JsonProperty
    int totalSales;

    @JsonProperty
    String productCategory;

}
