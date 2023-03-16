package org.pl.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.pl.entities.Customer;
import org.pl.entities.Sales;
import org.pl.serde.JSONSerde;
import org.pl.utils.ProducerUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class ProducerNew {

    static Gson gson = new Gson();

    private static Customer parseCustomer(JSONObject customerObj){
        Customer customer1 = gson.fromJson(String.valueOf(customerObj), Customer.class);
        return customer1;
    }
    private static Sales parseSales(JSONObject salesObj){
        Sales sales1 = gson.fromJson(String.valueOf(salesObj), Sales.class);
        return sales1;
    }

    public static List<Sales> readSalesFromFile(){
        JSONParser jsonParser =  new JSONParser();
        try(FileReader salesReader = new FileReader("data-ingestion\\src\\main\\resources\\salesData.json")) {

            Object salesObj = jsonParser.parse(salesReader);
            JSONArray salesList = (JSONArray) salesObj;
            List<Sales> allSales = (List<Sales>) salesList
                    .stream()
                    .map(sale -> parseSales((JSONObject) sale))
                    .collect(Collectors.toList());
            return allSales;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }
    public static List<Customer> readCustomersFromFile(){
        JSONParser jsonParser =  new JSONParser();
        try(FileReader customerReader = new FileReader("data-ingestion\\src\\main\\resources\\customerData.json"); FileReader salesReader = new FileReader("data-ingestion\\src\\main\\resources\\salesData.json")){

            Object customerObj = jsonParser.parse(customerReader);
            JSONArray customerList = (JSONArray) customerObj;
            List<Customer> allCustomers = (List<Customer>) customerList
                    .stream()
                    .map(customer -> parseCustomer((JSONObject) customer))
                    .collect(Collectors.toList());


            return allCustomers;
        } catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }
    static Callback callback = (metadata, exception) -> {
        if(exception != null) {
            System.out.printf("Producing records encountered error %s %n", exception);
        } else {
            System.out.printf("Record produced - offset - %d timestamp - %d %n", metadata.offset(), metadata.timestamp());
        }
    };

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();

        // setup producer
        Properties properties = ProducerUtils.loadProperties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JSONSerde.class);
        // read json file
        List<Customer> allCustomers = readCustomersFromFile();
        List<Sales> allSales = readSalesFromFile();
        // send data
        try(Admin adminClient = Admin.create(properties);
            org.apache.kafka.clients.producer.Producer<Integer, JsonNode> producer = new KafkaProducer<>(properties)) {
            final String inputCustomerTopic = properties.getProperty("input.customer.topic");
            final String inputSalesTopic = properties.getProperty("input.sales.topic");
            List<ProducerRecord<Integer,JsonNode>> records = allCustomers.stream()
                    .map(customer -> new ProducerRecord<>(inputCustomerTopic, customer.getCustomerId(), mapper.convertValue(customer, JsonNode.class)))
                    .collect(Collectors.toList());
            records.addAll(allSales.stream()
                    .map(sale ->
                            new ProducerRecord<>(inputSalesTopic, sale.getSalesId(), mapper.convertValue(sale, JsonNode.class)))
                    .collect(Collectors.toList()));
            records.forEach(record -> producer.send(record, callback));
            producer.close(Duration.ofSeconds(5));
        }
    }
}
