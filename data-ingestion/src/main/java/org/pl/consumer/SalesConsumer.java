package org.pl.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.pl.serde.JSONSerde;

import java.util.Properties;


public class SalesConsumer {
    private static String topic="sales-data-topic";

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "KStreamDemo");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.IntegerSerde.class);
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JSONSerde.class);
        config.put(StreamsConfig.CLIENT_ID_CONFIG, "SalesConsumerData");


        StreamsBuilder streamBuilder = new StreamsBuilder();
        KStream<Integer, JsonNode> stream = streamBuilder.stream(topic, Consumed.with(Serdes.Integer(), new JSONSerde()));
        stream.foreach((key, value) -> System.out.printf("key = %d, value = %s%n", key, value));

        KafkaStreams kafkaStreams = new KafkaStreams(streamBuilder.build(), config);
        kafkaStreams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));

//        KafkaConsumer<Integer, String> kafkaConsumer = new KafkaConsumer<Integer, String>(config);
//        kafkaConsumer.subscribe(Arrays.asList("Sales"));
//
//        while (true) {
//        ConsumerRecords<Integer, String> records = kafkaConsumer.poll(Duration.ZERO);
//
//        for (ConsumerRecord<Integer, String> rec : records) {
//            System.out.println("CustomerId : " + rec.key() + " CustomerValue :" + rec.value());
//            }
//        }
    }
}
