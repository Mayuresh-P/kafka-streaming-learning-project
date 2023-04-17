package org.pl.producer;

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
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class DataIngestionProducer {

    public static final Logger log = LoggerFactory.getLogger(DataIngestionProducer.class.getSimpleName());

    public DataIngestionProducer() throws IOException {
    }

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

    // load producer properties
    Properties properties = loadProperties();

    String customerInputTopic = properties.getProperty("input.customer.topic");

    String salesInputTopic = properties.getProperty("input.sales.topic");

    public void produce(Object obj) throws IOException {


        if (obj.getClass() == Customer.class) {

            Customer customer = (Customer) obj;

            // setup new kafka producer for customer
            KafkaProducer<Integer, Customer> customerProducer = new KafkaProducer<>(properties);

            //create a producer record for customer
            ProducerRecord<Integer, Customer> customerProducerRecord = new ProducerRecord<>(
                    customerInputTopic, customer.getCustomerId(), customer);

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

        else if (obj.getClass() == Sales.class) {

            // setup new kafka producer for sales
            KafkaProducer<Integer, Sales> salesProducer = new KafkaProducer<>(properties);

            Sales sale = (Sales) obj;

            // create a producer record for sales
            ProducerRecord<Integer, Sales> salesProducerRecord = new ProducerRecord<>(
                    salesInputTopic, sale.getSalesId(), sale
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

        else {

            // setup new kafka producer
            KafkaProducer<Integer, Sales> salesProducer = new KafkaProducer<>(properties);

            // get data from file
            List<Sales> allSales = readFromFileForSales();

            // get customer data from file
            List<Customer> allCustomer = readFromFileForCustomer();

            // create a producer record for sales
            List<ProducerRecord<Integer, Sales>> salesRecords = allSales
                    .stream()
                    .map(
                            sale -> new ProducerRecord<>(salesInputTopic, sale.getSalesId(), sale)
                    )
                    .collect(Collectors.toList());

            // send data to topic
            salesRecords.forEach(record -> salesProducer.send(record, (metadata, exception) -> {

                if (exception == null) {
                    log.info("Key: " + record.key() + " Topic: " + record.topic()
                    );
                } else {
                    log.error("Error while producing: " + exception);
                }
            }));

            // create kafka producer for customer
            KafkaProducer<Integer, Customer> customerProducer = new KafkaProducer<>(properties);

            //create a producer record for customer
            List<ProducerRecord<Integer, Customer>> customerRecords = allCustomer
                    .stream()
                    .map(
                            customer -> new ProducerRecord<>(customerInputTopic, customer.getCustomerId(), customer)
                    )
                    .collect(Collectors.toList());

            // send data to customer topic
            customerRecords.forEach(record -> customerProducer.send(record, ((metadata, exception) -> {

                if(exception == null) {
                    log.info("Key: " + record.key() + " Topic: " + record.topic()
                    );
                } else {
                    log.error("Error while producing: " + exception);
                }
            })));

            // close the producer
            salesProducer.close();
            customerProducer.close();

        }

    }

}
