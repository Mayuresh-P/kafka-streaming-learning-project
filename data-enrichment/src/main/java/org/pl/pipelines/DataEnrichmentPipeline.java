package org.pl.pipelines;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.pl.serde.CustomSerde;
import org.pl.util.ApplicationProperties;
import org.pl.util.TopologyUtils;
import org.pl.aggregator.SalesByCustomerAgeAggregator;
import org.pl.aggregator.TotalSalesByCategoryAggregator;
import org.pl.entities.CustomerSales;
import org.pl.entities.SalesByCustomerAge;
import org.pl.entities.TotalSalesByCategory;
import org.pl.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DataEnrichmentPipeline {
//    private static final Serde<CustomerSales> customerSalesSerde = new CustomSerde<>(CustomerSales.class).generateSerde();
//    private static final Serde<TotalSalesByCategory> totalSalesByCategorySerde = new CustomSerde<>(TotalSalesByCategory.class).generateSerde();

    Logger logger = LoggerFactory.getLogger(DataEnrichmentPipeline.class.getSimpleName());

    @Inject
    ApplicationProperties applicationProperties;

    private static final String TOTAL_SALES_BY_CATEGORY_STORE = "total-sales-by-category-store";
    private static final String SALES_BY_CUSTOMER_AGE = "sales-by-customer-age-store";

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
                .peek((key, value) -> logger.info("Key: " + key+" ,Value: "+ value.toString()));

        totalSalesByCategoryKStream.to(applicationProperties.getTotalSalesTopic(), Produced.with(Serdes.String(), new CustomSerde<>(TotalSalesByCategory.class).generateSerde()));
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
                .peek((key, value) -> logger.info("Key: " + key+" ,Value: "+value.toString()))
                .to(applicationProperties.getCustomerSalesAgeTopic(), Produced.with(Serdes.String(), new CustomSerde<>(SalesByCustomerAge.class).generateSerde()));
    }

}
