package org.pl.pipelines;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.pl.TopologyUtils;
import org.pl.aggregator.SalesByCustomerAgeAggregator;
import org.pl.aggregator.TotalSalesByCategoryAggregator;
import org.pl.entities.CustomerSales;
import org.pl.entities.SalesByCustomerAge;
import org.pl.entities.TotalSalesByCategory;
import org.pl.serde.CustomSerde;
import org.pl.serde.JSONSerde;
import org.pl.utils.Utils;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DataEnrichmentPipeline {
//    private static final Serde<CustomerSales> customerSalesSerde = new CustomSerde<>(CustomerSales.class).generateSerde();
//    private static final Serde<TotalSalesByCategory> totalSalesByCategorySerde = new CustomSerde<>(TotalSalesByCategory.class).generateSerde();

    private static final String TOTAL_SALES_BY_CATEGORY_STORE = "total-sales-by-category-store";
    private static final String SALES_BY_CUSTOMER_AGE = "sales-by-customer-age-store";
    public void generateTotalSalesByCategory(KStream<Integer, CustomerSales> customerSalesKStream){
        KStream<String, TotalSalesByCategory> totalSalesByCategoryKStream = customerSalesKStream
            .selectKey((key, customerSales)-> customerSales.getProductCategory().toLowerCase())
            .groupByKey(Grouped.with(Serdes.String(), new JSONSerde<>(CustomerSales.class)))
            .aggregate(
                    TotalSalesByCategory::new,
                    new TotalSalesByCategoryAggregator(),
                    TopologyUtils.materialize(TotalSalesByCategory.class, TOTAL_SALES_BY_CATEGORY_STORE, false)
            )
            .toStream()
            .peek((key, value) -> System.out.println(key+" "+ value));

        totalSalesByCategoryKStream.to("total-sales-by-category", Produced.with(Serdes.String(), new JSONSerde<>(TotalSalesByCategory.class)));
    }

    public void generateTotalSalesByCustomerAge(KStream<Integer, CustomerSales> customerSalesKStream){
        customerSalesKStream
                .selectKey((csKey, csValue) -> Utils.getCustomerSegment(csValue.getAge()))
                .groupByKey(Grouped.with(Serdes.String(), new JSONSerde<>(CustomerSales.class)))
                .aggregate(
                        SalesByCustomerAge::new,
                        new SalesByCustomerAgeAggregator(),
                        TopologyUtils.materialize(SalesByCustomerAge.class, SALES_BY_CUSTOMER_AGE, false)
                )
                .toStream()
                .peek((key, value) -> System.out.println(key+" "+value))
                .to("sales-by-customer-age", Produced.with(Serdes.String(), new JSONSerde<>(SalesByCustomerAge.class)));
    }
}
