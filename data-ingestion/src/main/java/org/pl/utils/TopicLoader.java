package org.pl.utils;


import io.vertx.core.eventbus.impl.clustered.Serializer;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.pl.entities.Customer;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class TopicLoader {

    public static void main(String[] args) throws IOException {
        runProducer();
    }

    public static void runProducer() throws IOException {
        Properties properties = ProducerUtils.loadProperties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Customer.class);

        try(Admin adminClient = Admin.create(properties);
            Producer<String, String> producer = new KafkaProducer<>(properties)) {
            final String inputTopic = properties.getProperty("ktable.input.topic");
            final String outputTopic = properties.getProperty("ktable.output.topic");
            var topics = List.of(ProducerUtils.createTopic(inputTopic), ProducerUtils.createTopic(outputTopic));
            adminClient.createTopics(topics);

            Callback callback = (metadata, exception) -> {
                if(exception != null) {
                    System.out.printf("Producing records encountered error %s %n", exception);
                } else {
                    System.out.printf("Record produced - offset - %d timestamp - %d %n", metadata.offset(), metadata.timestamp());
                }

            };

            var rawRecords = List.of("orderNumber-1001",
                    "orderNumber-5000",
                    "orderNumber-999",
                    "orderNumber-3330",
                    "bogus-1",
                    "bogus-2",
                    "orderNumber-1400");
            var producerRecords = rawRecords.stream().map(r -> new ProducerRecord<>(inputTopic,"order-key", r)).collect(Collectors.toList());
            producerRecords.forEach((pr -> producer.send(pr, callback)));


        }
    }
}
