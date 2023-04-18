package org.pl;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.pl.aggregator.TotalSalesByCategoryAggregator;
import org.pl.entities.*;
import org.pl.pipelines.DataEnrichmentPipeline;
import org.pl.util.TopologyUtils;
import org.pl.joiner.Joiner;
import org.pl.serde.CustomSerde;
import org.pl.util.ApplicationProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@ApplicationScoped

public class TopologyProducer {

    @Inject
    ApplicationProperties applicationProperties;

    @Inject
    DataEnrichmentPipeline dataEnrichmentPipeline;

    private static final Serde<Sales> salesSerde = new CustomSerde<>(Sales.class).generateSerde();
//            createSerdeForSalesValue();
    private static final Serde<Customer> customerSerde = new CustomSerde<>(Customer.class).generateSerde();
//        createSerdeForCustomerValue();
    private static final Serde<CustomerSales> customerSalesSerde = new CustomSerde<>(CustomerSales.class).generateSerde();
//        createSerdeForAllDataValue();

    private static final Serde<TotalSalesByCategory> totalSalesByCategory = new CustomSerde<>(TotalSalesByCategory.class).generateSerde();

    private static final String TOTAL_SALES_BY_CATEGORY_STORE = "total-sales-by-category-store";

    @Produces
    public Topology buildTopology() {

        KeyValueBytesStoreSupplier storeSupplierForTopicSink = Stores
                .persistentKeyValueStore(applicationProperties.getTopicSink());

        StreamsBuilder builder = new StreamsBuilder();

        KStream<Integer, Sales> salesStream = builder
                .stream(
                        applicationProperties.getKafkaStreamsTopicSource(), Consumed.with(Serdes.Integer(), salesSerde)
                );

        GlobalKTable<Integer, Customer> customerTable = builder
                    .globalTable(
                            applicationProperties.getCustomerTopic(), Materialized.<Integer, Customer>as(storeSupplierForTopicSink)
                            .withKeySerde(Serdes.Integer())
                            .withValueSerde(customerSerde)
                    );

        salesStream
                .selectKey(
                        (key, value) -> value.getCustomerId()
                )
                .join(customerTable,
                        (salesKey, salesValue) -> salesValue.getCustomerId(),
                        (salesValue, customerValue) -> new Joiner().joinAllData(customerValue, salesValue)
                )
                .to(applicationProperties.getTopicSink(),
                        Produced.with(Serdes.Integer(), customerSalesSerde));

        // TOTAL SALES

        KStream<String, TotalSalesByCategory> totalSalesByCategoryKStream = builder.stream(
                applicationProperties.getTopicSink(), Consumed.with(Serdes.Integer(), customerSalesSerde)
        )
                .selectKey(
                    (k, v) -> v.getProductCategory().toLowerCase())
                .groupByKey(
                    Grouped.with(Serdes.String(), customerSalesSerde)
                ).aggregate(
                        TotalSalesByCategory::new,
                        new TotalSalesByCategoryAggregator(),
                        TopologyUtils.materialize(TotalSalesByCategory.class, TOTAL_SALES_BY_CATEGORY_STORE, true)
                ).toStream()
                .peek((k, v) -> System.out.println("key: " + k + ", value: " + v));

        totalSalesByCategoryKStream.to(applicationProperties.getTotalSalesTopic(), Produced.with(Serdes.String(), totalSalesByCategory));

        KStream<Integer, CustomerSales> customerSalesKStream = builder.stream("customer-sales", Consumed.with(Serdes.Integer(), customerSalesSerde));

        dataEnrichmentPipeline.generateTotalSalesByCustomerAge(customerSalesKStream);

        dataEnrichmentPipeline.generateTotalSalesByCustomerGender(customerSalesKStream);



        Topology topology = builder.build();

        return topology;

    }

}
