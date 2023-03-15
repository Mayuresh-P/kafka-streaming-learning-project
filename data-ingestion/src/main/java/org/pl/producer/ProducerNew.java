package org.pl.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
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
import org.pl.serde.JSONSerde;
import org.pl.utils.ProducerUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class ProducerNew {

    private static Customer parseCustomer(JSONObject custObj){
        Gson gson = new Gson();
        Customer customer1 = gson.fromJson(String.valueOf(custObj), Customer.class);
//        customer1.setCustomerId(Integer.parseInt((String) customer.get("customerId")));
//        customer1.setAge(Integer.parseInt((String) customer.get("age")));
//        customer1.setGender((String) customer.get("gender"));
//        customer1.setName((String) customer.get("name"));
//        System.out.println(customer1.getName());
        return customer1;
    }
    public static List<Customer> readFromFile(){
        JSONParser jsonParser =  new JSONParser();
        try(FileReader reader = new FileReader("data-ingestion\\src\\main\\resources\\customerData.json")){

            Object obj = jsonParser.parse(reader);
            JSONArray customerList = (JSONArray) obj;

            List<Customer> allCustomers = (List<Customer>) customerList.stream().map(customer -> parseCustomer((JSONObject) customer)).collect(Collectors.toList());
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
//        read json file
        List<Customer> allCustomers = readFromFile();
        // send data
        try(Admin adminClient = Admin.create(properties);
            org.apache.kafka.clients.producer.Producer<Integer, JsonNode> producer = new KafkaProducer<>(properties)) {
            final String inputTopic = properties.getProperty("input.customer.topic");
            List<ProducerRecord<Integer,JsonNode>> records = allCustomers.stream().map(customer -> new ProducerRecord<>(inputTopic, customer.getCustomerId(), mapper.convertValue(customer, JsonNode.class))).collect(Collectors.toList());
            records.forEach(record -> producer.send(record, callback));
//            adminClient.
        }
    }
}