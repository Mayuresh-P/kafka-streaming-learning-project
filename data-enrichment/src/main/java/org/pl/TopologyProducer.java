package org.pl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import org.apache.kafka.streams.state.*;
import org.pl.entities.Customer;
import org.pl.entities.CustomerSales;
import org.pl.entities.Sales;
import org.pl.joiner.SalesCustomerJoiner;
import org.pl.serde.JSONSerde;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;


@ApplicationScoped
public class TopologyProducer {

    private final static Logger logger = LoggerFactory.getLogger(TopologyProducer.class.getName());
    private static final String CUSTOMER_TOPIC = "customer-data";
    private static final String SALES_TOPIC = "sales-data";

    private static final String SALES_CUSTOMER_STORE = "customer-sales-store";

    final ForeachAction<Integer, JsonNode> loggingForEach = (key, value) -> {
        if (value != null)
            logger.info("Key: {}, Value: {}", key, value);
    };


    @Produces
    public Topology buildTopology() {
        final StreamsBuilder builder = new StreamsBuilder();

        KeyValueBytesStoreSupplier storeSupplier = Stores.persistentKeyValueStore(SALES_CUSTOMER_STORE);

        final ObjectMapper mapper = new ObjectMapper();

        final GlobalKTable<Integer, JsonNode> customers = builder.globalTable(CUSTOMER_TOPIC, Materialized.<Integer, JsonNode>as(storeSupplier)
                .withKeySerde(Serdes.Integer())
                .withValueSerde(new JSONSerde())
        );

        final KStream<Integer, JsonNode> salesStream = builder.stream(SALES_TOPIC, Consumed.with(Serdes.Integer(),new JSONSerde()));


        salesStream
                .selectKey((key, value) -> value.get("customerId").asInt())
                .join(customers,
                (salesKey, salesValue) -> salesValue.get("customerId").asInt(),
                (salesValue, customerValue) -> mapper.convertValue(
                        new CustomerSales(
                                mapper.convertValue(customerValue, Customer.class),
                                mapper.convertValue(salesValue, Sales.class)
                        ),   JsonNode.class))
                .peek(loggingForEach)
                .to("customer-sales", Produced.with(Serdes.Integer(), new JSONSerde()));


//        builder.stream("customer-sales").


        Topology build = builder.build();
        logger.info(build.describe().toString());


        return build;
    }
}

//        ValueJoiner<JsonNode, JsonNode, JsonNode> customerSalesJoiner = (salesJSON, customerJSON) -> {
//            Customer customer = null;
//            Sales sales = null;
//            try{
////                customer = mapper.treeToValue(customerJSON, Customer.class);
//                customer = mapper.treeToValue(customerJSON, Customer.class);
//                sales = mapper.treeToValue(salesJSON, Sales.class)
//                CustomerSales updatedCustomerSales = new CustomerSales();
//                updatedCustomerSales.setSalesId(sales.getSalesId());
//                updatedCustomerSales.setProductCategory(sales.getProductCategory());
//                updatedCustomerSales.setTimestamp(sales.getTimestamp());
//                updatedCustomerSales.setQuantity(sales.getQuantity());
//                updatedCustomerSales.setPrice(sales.getPrice());
//                updatedCustomerSales.setProductId(sales.getProductId());
//                updatedCustomerSales.setCustomerId(sales.getCustomerId());
//                if(customer != null){
//                    updatedCustomerSales.setAge(customer.getAge());
//                    updatedCustomerSales.setGender(customer.getGender());
//                    updatedCustomerSales.setName(customer.getName());
//                }
//                return updatedCustomerSales;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//
//        };

//        KeyValueMapper<Integer,JsonNode, JsonNode> customerSalesKeyValueMapper = (saleId, customer)-> {
//
////            CustomerSales customerSales = new SalesCustomerJoiner().apply(saleId,customer);
////            JsonNode customerSalesJson = mapper.convertValue(customerSales, JsonNode.class);
//            KeyValue customerSalesKV = KeyValue.pair(saleId, customer);
//            return customerSalesKV;
//        };

//        final KStream<Integer,JsonNode> customerSalesStream =
//                salesStream
//                        .leftJoin(customerTable,
//                                (salesKey, salesValue)-> salesKey,
//                                (sales, customer)-> {
//                                    try {
//                                        return mapper.convertValue(new SalesCustomerJoiner()
//                                                .apply(mapper.treeToValue(sales, Sales.class),mapper.treeToValue(customer, Customer.class)), JsonNode.class);
//                                    } catch (JsonProcessingException e) {
//                                        throw new RuntimeException(e);
//                                    }
//                                });
//        customerSalesStream
//                        .to("customer-sales",Produced.with(new Serdes.IntegerSerde(), new JSONSerde()));
//        customerSalesStream.peek((key,value)->System.out.println(key+" "+value));
//        final KStream<Integer, JsonNode> customerSales = builder.stream("customer-sales", Consumed.with(Serdes.Integer(), new JSONSerde()));
//        customerSales.print(Printed.toSysOut());
//Map<String, String> changeLogConfig = new HashMap<>();
//    KeyValueBytesStoreSupplier storeSupplier = Stores.persistentKeyValueStore("customerSalesStore");
//    StoreBuilder<KeyValueStore<Integer, JsonNode>> stateStore = Stores.keyValueStoreBuilder(storeSupplier, Serdes.Integer(), new JSONSerde()).withLoggingEnabled(changeLogConfig);
//    final SalesCustomerJoiner joiner = new SalesCustomerJoiner();
//    final KStream<Integer, JsonNode> salesStream =
//            builder.stream(SALES_TOPIC, Consumed.with(Serdes.Integer(), new JSONSerde())).peek((key, value)-> System.out.println(key.toString()+" "+value.toString()))
//            ;
//    final GlobalKTable<Integer, JsonNode> customerTable = builder.globalTable(CUSTOMER_TOPIC, Consumed.with(Serdes.Integer(), new JSONSerde()));
//        salesStream.peek((key,value)-> System.out.println(key+" "+value));
