package org.pl.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.kafka.clients.producer.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.pl.entities.Customer;
import org.pl.entities.Sales;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class DataIngestionProducer {

    public static final Logger log = LoggerFactory.getLogger(DataIngestionProducer.class.getSimpleName());

    public Properties loadProperties() throws IOException {

        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("src\\main\\resources\\producer.properties")) {
            properties.load(fis);
            return properties;
        }

    }

    public static List<Sales> readFromFileForSales() {
        JSONParser parser = new JSONParser();
        try(FileReader reader = new FileReader("src\\main\\resources\\salesData.json")) {
            Object obj = parser.parse(reader);
            JSONArray listSales = (JSONArray) obj;

            return (List<Sales>) listSales.stream().map(sale -> parseSales( (JSONObject) sale)).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Customer> readFromFileForCustomer() {
        JSONParser parser = new JSONParser();
        try(FileReader reader = new FileReader("src\\main\\resources\\customerData.json")) {
            Object obj = parser.parse(reader);
            JSONArray listCustomer = (JSONArray) obj;

            return (List<Customer>) listCustomer.stream().map(customer -> parseCustomer( (JSONObject) customer)).collect(Collectors.toList());
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object parseCustomer(JSONObject customerObject) {

        Gson gson = new Gson();
        Customer customer = gson.fromJson(String.valueOf(customerObject), Customer.class);

        return customer;

    }

    private static Object parseSales(JSONObject saleObj) {
        Gson gson = new Gson();
        Sales sale = gson.fromJson(String.valueOf(saleObj), Sales.class);

        return sale;

    }

    public void produce() throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        // load producer properties
        Properties properties = loadProperties();

        // setup new kafka producer for sales
        KafkaProducer<Integer, JsonNode> salesProducer = new KafkaProducer<>(properties);

        // setup new kafka producer for customer
        KafkaProducer<Integer, JsonNode> customerProducer = new KafkaProducer<>(properties);

        // get data from file
        List<Sales> allSales = readFromFileForSales();

        // get customer data from file
        List<Customer> allCustomer = readFromFileForCustomer();

        String customerInputTopic = properties.getProperty("input.customer.topic");

        String salesInputTopic = properties.getProperty("input.sales.topic");

        // create a producer record for sales
        List<ProducerRecord<Integer, JsonNode>> salesRecords = allSales
                .stream()
                .map(
                        sale -> new ProducerRecord<>(salesInputTopic, sale.getSalesId(), mapper.convertValue(sale, JsonNode.class))
                )
                .collect(Collectors.toList());

        // send data to topic
        salesRecords.forEach(record -> salesProducer.send(record, (metadata, exception) -> {

            if (exception == null) {
                log.info("Key: " + record.key());
            } else {
                log.error("Error while producing: " + exception);
            }
        }));

        //create a producer record for customer
        List<ProducerRecord<Integer, JsonNode>> customerRecords = allCustomer
                .stream()
                        .map(
                                customer -> new ProducerRecord<>(customerInputTopic, customer.getCustomerId(), mapper.convertValue(customer, JsonNode.class))
                        )
                                .collect(Collectors.toList());

        // send data to customer topic
        customerRecords.forEach(record -> customerProducer.send(record, ((metadata, exception) -> {

            if(exception == null) {
                log.info("Key: " + record.key());
            } else {
                log.error("Error while producing: " + exception);
            }
        })));

        salesProducer.close();

        customerProducer.close();

    }

    public void produceCustomer(Customer customer) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        // load producer properties
        Properties properties = loadProperties();

        // setup new kafka producer for customer
        KafkaProducer<Integer, JsonNode> customerProducer = new KafkaProducer<>(properties);

        String customerInputTopic = properties.getProperty("input.customer.topic");

        //create a producer record for customer
        ProducerRecord<Integer, JsonNode> customerProducerRecord = new ProducerRecord<>(
                customerInputTopic, customer.getCustomerId(), mapper.convertValue(customer, JsonNode.class));

        // produce data to customer topic
        customerProducer.send(customerProducerRecord, ((metadata, exception) -> {

            if(exception == null) {
                log.info("Key: " + customerProducerRecord.key());
            } else {
                log.error("Error while producing: " + exception);
            }
        }));

        // close the producer
        customerProducer.close();

    }

    public void produceSales(Sales sale) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        // load producer properties
        Properties properties = loadProperties();

        // setup new kafka producer for sales
        KafkaProducer<Integer, JsonNode> salesProducer = new KafkaProducer<>(properties);

        String salesInputTopic = properties.getProperty("input.sales.topic");

        // create a producer record for sales
        ProducerRecord<Integer, JsonNode> salesProducerRecord = new ProducerRecord<>(
                salesInputTopic, sale.getSalesId(), mapper.convertValue(sale, JsonNode.class)
        );

        // produce data to customer topic
        salesProducer.send(salesProducerRecord, ((metadata, exception) -> {

            if(exception == null) {
                log.info("Key: " + salesProducerRecord.key());
            } else {
                log.error("Error while producing: " + exception);
            }
        }));

        // close the producer
        salesProducer.close();

    }

}
