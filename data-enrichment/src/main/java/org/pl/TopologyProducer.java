package org.pl;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.pl.pipelines.DataEnrichmentPipeline;
import org.pl.entities.CustomerSales;
import org.pl.entities.Customer;
import org.pl.entities.Sales;
import org.pl.entities.TotalSalesByCategory;
import org.pl.joiner.Joiner;
import org.pl.serde.CustomSerde;
import org.pl.util.ApplicationProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@ApplicationScoped
@Path("/api")
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
    @POST
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

        KStream<Integer, CustomerSales> customerSalesKStream = builder.stream("customer-sales", Consumed.with(Serdes.Integer(), customerSalesSerde));

        dataEnrichmentPipeline.generateTotalSalesByCategory(customerSalesKStream);

        dataEnrichmentPipeline.generateTotalSalesByCustomerAge(customerSalesKStream);

        Topology topology = builder.build();

        return topology;

    }

}
