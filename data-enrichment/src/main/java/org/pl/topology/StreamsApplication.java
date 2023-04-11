package org.pl.topology;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.pl.entities.AllData;
import org.pl.entities.Customer;
import org.pl.entities.Sales;
import org.pl.entities.TotalSales;
import org.pl.joiner.Joiner;
import org.pl.serialization.JsonDeserializer;
import org.pl.serialization.JsonSerializer;
import org.pl.util.ApplicationProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@ApplicationScoped
@Path("/api")
public class StreamsApplication {

    @Inject
    ApplicationProperties applicationProperties;

    private static final Serde<Sales> salesSerde = createSerdeForSalesValue();
    private static final Serde<Customer> customerSerde = createSerdeForCustomerValue();
    private static final Serde<AllData> allDataSerde = createSerdeForAllDataValue();

    private static final Serde<TotalSales> totalSalesSerde = createSerdeForTotalSalesValue();

    @Produces
    @POST
    @Path("/startStream")
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
                        Produced.with(Serdes.Integer(), allDataSerde));


        // TOTAL SALES

        Topology topology = builder.build();

        return topology;

    }

    // create serde for allData
    private static Serde<AllData> createSerdeForAllDataValue() {

        Serializer<AllData> allDataSerializer = new JsonSerializer<>();
        Deserializer<AllData> allDataDeserializer = new JsonDeserializer<>(AllData.class);

        return Serdes.serdeFrom(allDataSerializer, allDataDeserializer);
    }

    // create serde for sales
    private static Serde<Sales> createSerdeForSalesValue() {

        Serializer<Sales> salesSerializer = new JsonSerializer<>();
        Deserializer<Sales> salesDeserializer = new JsonDeserializer<>(Sales.class);

        return Serdes.serdeFrom(salesSerializer, salesDeserializer);

    }

    // create serde for customer
    private static Serde<Customer> createSerdeForCustomerValue() {

        Serializer<Customer> customerSerializer = new JsonSerializer<>();
        Deserializer<Customer> customerDeserializer = new JsonDeserializer<>(Customer.class);

        return Serdes.serdeFrom(customerSerializer, customerDeserializer);

    }

    // create serde for totalSales
    private static Serde<TotalSales> createSerdeForTotalSalesValue() {

        Serializer<TotalSales> totalSalesSerializer = new JsonSerializer<>();
        Deserializer<TotalSales> totalSalesDeserializer = new JsonDeserializer<>(TotalSales.class);

        return Serdes.serdeFrom(totalSalesSerializer, totalSalesDeserializer);
    }

}
