package org.pl.pipelines;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.pl.aggregator.SalesByCustomerGenderAggregator;
import org.pl.entities.SalesByCustomerGender;
import org.pl.serde.CustomSerde;
import org.pl.util.TopologyUtils;
import org.pl.aggregator.SalesByCustomerAgeAggregator;
import org.pl.aggregator.TotalSalesByCategoryAggregator;
import org.pl.entities.CustomerSales;
import org.pl.entities.SalesByCustomerAge;
import org.pl.entities.TotalSalesByCategory;
import org.pl.serde.JSONSerde;
import org.pl.util.Utils;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DataEnrichmentPipeline {
//    private static final Serde<CustomerSales> customerSalesSerde = new CustomSerde<>(CustomerSales.class).generateSerde();
//    private static final Serde<TotalSalesByCategory> totalSalesByCategorySerde = new CustomSerde<>(TotalSalesByCategory.class).generateSerde();

    private static final String TOTAL_SALES_BY_CATEGORY_STORE = "total-sales-by-category-store";
    private static final String SALES_BY_CUSTOMER_AGE = "sales-by-customer-age-store";

    private static final String SALES_BY_CUSTOMER_GENDER = "sales-by-customer-gender-store";

    public void generateTotalSalesByCategory(KStream<Integer, CustomerSales> customerSalesKStream){
        KStream<String, TotalSalesByCategory> totalSalesByCategoryKStream = customerSalesKStream
                .selectKey((key, customerSales)-> customerSales.getProductCategory().toLowerCase())
                .groupByKey(Grouped.with(Serdes.String(), new CustomSerde<>(CustomerSales.class).generateSerde()))
                .aggregate(
                        TotalSalesByCategory::new,
                        new TotalSalesByCategoryAggregator(),
                        TopologyUtils.materialize(TotalSalesByCategory.class, TOTAL_SALES_BY_CATEGORY_STORE, true)
                )
                .toStream()
                .peek((key, value) -> System.out.println(key+" "+ value));

        totalSalesByCategoryKStream.to("total-sales-by-category", Produced.with(Serdes.String(), new CustomSerde<>(TotalSalesByCategory.class).generateSerde()));
    }

    public void generateTotalSalesByCustomerAge(KStream<Integer, CustomerSales> customerSalesKStream){
        customerSalesKStream
                .selectKey((csKey, csValue) -> Utils.getCustomerSegment(csValue.getAge()))
                .groupByKey(Grouped.with(Serdes.String(), new CustomSerde<>(CustomerSales.class).generateSerde()))
                .aggregate(
                        SalesByCustomerAge::new,
                        new SalesByCustomerAgeAggregator(),
                        TopologyUtils.materialize(SalesByCustomerAge.class, SALES_BY_CUSTOMER_AGE, true)
                )
                .toStream()
                .peek((key, value) -> System.out.println(key+" "+value))
                .to("sales-by-customer-age", Produced.with(Serdes.String(), new CustomSerde<>(SalesByCustomerAge.class).generateSerde()));
    }

    public void generateTotalSalesByCustomerGender(KStream<Integer, CustomerSales> customerSalesKStream){
        customerSalesKStream
                .selectKey((csKey, csValue) -> csValue.getGender())
                .groupByKey(Grouped.with(Serdes.String(), new CustomSerde<>(CustomerSales.class).generateSerde()))
                .aggregate(
                        SalesByCustomerGender::new,
                        new SalesByCustomerGenderAggregator(),
                        TopologyUtils.materialize(SalesByCustomerGender.class, SALES_BY_CUSTOMER_GENDER, true)
                )
                .toStream()
                .peek((key, value) -> System.out.println("SALES by customer gender: "+key+" "+value))
                .to("sales-by-customer-gender", Produced.with(Serdes.String(), new CustomSerde<>(SalesByCustomerGender.class).generateSerde()));

    }
}