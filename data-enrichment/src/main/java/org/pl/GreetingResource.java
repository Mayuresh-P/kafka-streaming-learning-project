//package org.pl;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.kafka.common.serialization.Serdes;
//import org.apache.kafka.streams.*;
//import org.apache.kafka.streams.kstream.*;
//import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
//import org.apache.kafka.streams.state.Stores;
//import org.pl.entities.Customer;
//import org.pl.entities.Sales;
//import org.pl.joiner.SalesCustomerJoiner;
//import org.pl.serde.JSONSerde;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.inject.Inject;
//import javax.ws.rs.GET;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.MediaType;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Properties;
//
//@Path("/api")
//public class GreetingResource {
//
//    private final String KAFKA_APP_ID = "dataenrichment";
//    private final String KAFKA_SERVER_NAME = "localhost:9092";
//    private final static Logger logger = LoggerFactory.getLogger(GreetingResource.class.getName());
//    private static final String CUSTOMER_TOPIC = "customer-data";
//    private static final String SALES_TOPIC = "sales-data";
//
//    private static final String SALES_CUSTOMER_TOPIC = "customer-sales";
//
//    private static final String SALES_CUSTOMER_STORE = "customer-sales-store";
//
//
//
//    ForeachAction<Integer, JsonNode> loggingForEach = (key, value) -> {
//        if (value != null)
//            logger.info("Key: {}, Value: {}", key, value);
//    };
//
//    @Inject
//    KafkaStreams streams;
//
//    @Inject
//    ObjectMapper mapper;
//
//    @Inject
//    InteractiveQueries interactiveQueries;
//
//
//
//
//    @POST
//    @Path("/start")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String startStreams(){
//        Properties props = new Properties();
//        props.put(StreamsConfig.APPLICATION_ID_CONFIG, KAFKA_APP_ID);
//        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER_NAME);
//        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass());
////        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, new JSONSerde(t).getClass());
//        final StreamsBuilder builder = new StreamsBuilder();
//
//        KeyValueBytesStoreSupplier storeSupplier = Stores.persistentKeyValueStore(SALES_CUSTOMER_STORE);
////        final GlobalKTable<Integer, JsonNode> customers= builder.globalTable(CUSTOMER_TOPIC, Materialized.<Integer, JsonNode>as(storeSupplier).withKeySerde(Serdes.Integer()).withValueSerde(new JSONSerde(t)));
//
//        final KStream<Integer, JsonNode> salesStream = builder.stream(SALES_TOPIC, Consumed.with(Serdes.Integer(),new JSONSerde(t)));
//
//        final KStream<Integer,JsonNode> customerSales = builder.stream(SALES_CUSTOMER_TOPIC, Consumed.with(Serdes.Integer(),new JSONSerde(t)));
//        salesStream.join(customers,
//                        (salesKey, salesValue) -> mapper.convertValue(salesValue, Sales.class).getCustomerId(),
//                        (salesValue, customerValue) -> mapper.convertValue(
//                                new SalesCustomerJoiner().apply(
//                                        mapper.convertValue(salesValue, Sales.class),
//                                        mapper.convertValue(customerValue, Customer.class)),
//                                JsonNode.class))
//                .peek(loggingForEach)
//                .to("customer-sales", Produced.with(Serdes.Integer(), new JSONSerde(t)));
//
//        Topology topology = builder.build();
//
//        streams = new KafkaStreams(topology, props);
//        interactiveQueries.setStream(streams);
//        streams.start();
//        return "Streams Started!";
//    }
//
//
//
//    @GET
//    @Path("/customers")
//    public String getCustomers() {
////        List<Customer> allCustomerSales = new ArrayList<>();
////        ReadOnlyKeyValueStore<Integer, JsonNode> store = interactiveQueries.getStore();
////        String mesg = "";
////        for (KeyValueIterator<Integer, JsonNode> it = store.all(); it.hasNext();) {
////            KeyValue<Integer, JsonNode> kv = it.next();
////            System.out.println(kv.key);
////            allCustomerSales.add(mapper.convertValue(kv.value, Customer.class));
//////            mesg = mesg.concat("\n").concat("Not Processed Records -- key: "+kv.key +" and value: "+kv.value);
////        }
//        return interactiveQueries.getCustomers();
//    }
//
//    @GET
//    @Path("/sales")
//    public List<Sales> getSales(){
//
//        List<Sales> allSales = new ArrayList<>();
//
//        return allSales;
//    }
//
////    @GET
////    @Produces(MediaType.TEXT_PLAIN)
////    public String hello() {
////        return interactiveQueries.getRecords();
////    }
//}