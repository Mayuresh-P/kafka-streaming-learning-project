package org.pl;
import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.*;
import org.pl.aggregator.TotalSalesByCategoryAggregator;
import org.pl.entities.Customer;
import org.pl.entities.CustomerSales;
import org.pl.entities.Sales;
import org.pl.entities.TotalSalesByCategory;
import org.pl.pipelines.DataEnrichmentPipeline;
import org.pl.serde.CustomSerde;
import org.pl.serde.JSONSerde;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;


@ApplicationScoped
public class TopologyProducer {

    private final static Logger logger = LoggerFactory.getLogger(TopologyProducer.class.getName());
    private static final String CUSTOMER_TOPIC = "customer-data";
    private static final String SALES_TOPIC = "sales-data";

    private static final String SALES_CUSTOMER_STORE = "customer-sales-store";
    private static final String TOTAL_SALES_BY_CATEGORY_STORE = "total-sales-by-category-store";
    final ForeachAction<Integer, CustomerSales> loggingForEach = (key, value) -> {
        if (value != null)
            logger.info("Key: {}, Value: {}", key, value);
    };


//    private static Serde<Customer> createCustomerSerde(){
//        Serializer<Customer> tSerializer = new JsonSerializer<Customer>();
//        Deserializer<Customer> tDeserializer = new JsonDeserializer<>(Customer.class);
//        return Serdes.serdeFrom(tSerializer, tDeserializer);
//    }
//    private static Serde<Sales> createSalesSerde(){
//        Serializer<Sales> tSerializer = new JsonSerializer<>();
//        Deserializer<Sales> tDeserializer = new JsonDeserializer<>(Sales.class);
//        return Serdes.serdeFrom(tSerializer, tDeserializer);
//    }
//
//    private static Serde<CustomerSales> createCustomerSalesSerde(){
//        Serializer<CustomerSales> tSerializer = new JsonSerializer<>();
//        Deserializer<CustomerSales> tDeserializer = new JsonDeserializer<>(CustomerSales.class);
//        return Serdes.serdeFrom(tSerializer, tDeserializer);
//    }

    private static final Serde<Customer> customerSerde = new CustomSerde<>(Customer.class).generateSerde();
    private static final Serde<Sales> salesSerde = new CustomSerde<>(Sales.class).generateSerde();
    private static final Serde<CustomerSales> customerSalesSerde = new CustomSerde<>(CustomerSales.class).generateSerde();

//    private static final Serde<TotalSalesByCategory> totalSalesByCategorySerde = new CustomSerde<>(TotalSalesByCategory.class).generateSerde();


    @Inject
    DataEnrichmentPipeline dataEnrichmentPipeline;


    @Produces
    public Topology buildTopology() {
        final StreamsBuilder builder = new StreamsBuilder();

        KeyValueBytesStoreSupplier storeSupplier = Stores.persistentKeyValueStore(SALES_CUSTOMER_STORE);


        final GlobalKTable<Integer, Customer> customers = builder.globalTable(CUSTOMER_TOPIC, Materialized.<Integer, Customer>as(storeSupplier)
                .withKeySerde(Serdes.Integer())
                .withValueSerde(customerSerde)
        );

        final KStream<Integer, Sales> salesStream = builder.stream(SALES_TOPIC, Consumed.with(Serdes.Integer(),salesSerde));


        salesStream
                .selectKey((key, value) -> value.getCustomerId())
                .join(customers,
                (salesKey, salesValue) -> salesValue.getCustomerId(),
                (salesValue, customerValue) ->
                        new CustomerSales(
                                customerValue,
                                salesValue
                        ))
                .peek(loggingForEach)
                .to("customer-sales", Produced.with(Serdes.Integer(), customerSalesSerde));

        KStream<Integer, CustomerSales> customerSalesKStream = builder.stream("customer-sales", Consumed.with(Serdes.Integer(), customerSalesSerde));


        dataEnrichmentPipeline.generateTotalSalesByCategory(customerSalesKStream);

        dataEnrichmentPipeline.generateTotalSalesByCustomerAge(customerSalesKStream);

//        KStream<String, TotalSalesByCategory> totalSalesByCategoryKStream = builder.stream("customer-sales",
//                Consumed.with(Serdes.Integer(), customerSalesSerde))
//                .selectKey((key, customerSales)-> customerSales.getProductCategory().toLowerCase())
//                .groupByKey(Grouped.with(Serdes.String(), customerSalesSerde))
//                .aggregate(
//                        TotalSalesByCategory::new,
//                        new TotalSalesByCategoryAggregator(),
//                        TopologyUtils.materialize(TotalSalesByCategory.class, TOTAL_SALES_BY_CATEGORY_STORE, false)
//                )
//                .toStream()
//                .peek((key, value) -> System.out.println(key+ " "+ value));
//
//        totalSalesByCategoryKStream.to("total-sales-by-category",Produced.with(Serdes.String(), totalSalesByCategorySerde));



        Topology build = builder.build();
        logger.info(build.describe().toString());
        return build;
    }
}