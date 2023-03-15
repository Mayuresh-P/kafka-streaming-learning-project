package org.pl.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.pl.entities.Customer;
import org.pl.serde.CustomerSerde;
import org.pl.utils.ProducerUtils;

import java.io.FileInputStream;
import java.util.Properties;

public class ConsumerStreams {



    public static Properties loadProperties() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("src\\main\\resources\\producer.properties")) {
            properties.load(fis);
            return properties;
        }
        catch (Exception e){
            System.out.println("Properties not loaded");
            return properties;
        }
    }

    public static void main() {
//        Properties properties = loadProperties();
//        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass());
//        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Customer.class.getName());
//        inputTopic = properties.getProperty("input.customer.topic");
//
//
//        KafkaStreams kafkaStreams =  new KafkaStreams(builder.build(), properties);
//        kafkaStreams.start();
    }

    public static void consume(){
        Properties properties = loadProperties();
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Customer.class.getName());
        String inputTopic = properties.getProperty("input.customer.topic");

        final StreamsBuilder builder = new StreamsBuilder();




        KStream<Integer, Customer> firstStream = builder.stream(inputTopic, Consumed.with(Serdes.Integer(), new CustomerSerde()));
        firstStream.peek((key, value) -> System.out.println("key "+key+" value "+value));
        KafkaStreams kafkaStreams =  new KafkaStreams(builder.build(), properties);
        kafkaStreams.start();

    }
}
