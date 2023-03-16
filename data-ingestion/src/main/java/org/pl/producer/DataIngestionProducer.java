package org.pl.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.kafka.clients.producer.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.pl.entities.Sales;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
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

    public static List<Sales> readFromFile() {
        JSONParser parser = new JSONParser();
        try(FileReader reader = new FileReader("C:\\Users\\msantosh\\Projects\\Quarkus Projects\\kafka-streaming-learning-project\\data-ingestion\\src\\main\\resources\\data.json")) {
            Object obj = parser.parse(reader);
            JSONArray listSales = (JSONArray) obj;

            List<Sales> allSales = (List<Sales>) listSales.stream().map(sale -> parseSales( (JSONObject) sale)).collect(Collectors.toList());
            return allSales;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

        // setup new kafka producer
        KafkaProducer<Integer, JsonNode> producer = new KafkaProducer<>(properties);

        // send data
        List<Sales> allSales = readFromFile();

        String inputTopic = properties.getProperty("input.sales.topic");

        List<ProducerRecord<Integer, JsonNode>> records = allSales
                .stream()
                .map(
                        sale -> new ProducerRecord<>(inputTopic, sale.getSalesId(), mapper.convertValue(sale, JsonNode.class))
                )
                .collect(Collectors.toList());


        records.forEach(record -> producer.send(record, (metadata, exception) -> {

            if (exception == null) {

            } else {
                log.info("Error while producing: " + exception);
            }
        }));

        producer.close();

    }

    public static void main(String[] args) {
        System.out.println(Instant.now());
        ZonedDateTime z = Instant.now().atZone(ZoneId.systemDefault());
        System.out.println(z);
    }

}
