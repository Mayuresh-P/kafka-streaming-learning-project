package org.pl.producer;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.pl.entities.Customer;
import org.pl.utils.ProducerUtils;
import org.pl.utils.TopicLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Properties;

public class Producer {

    static Callback callback = (metadata, exception) -> {
        if(exception != null) {
            System.out.printf("Producing records encountered error %s %n", exception);
        } else {
            System.out.printf("Record produced - offset - %d timestamp - %d %n", metadata.offset(), metadata.timestamp());
        }
    };


    public static final Logger logger = LoggerFactory.getLogger(Producer.class.getName());

    public static void produceToCustomer( Customer customer){
        Properties properties = ProducerUtils.loadProperties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Customer.class);

        try(Admin adminClient = Admin.create(properties);
            org.apache.kafka.clients.producer.Producer<Integer, Customer> producer = new KafkaProducer<>(properties)) {
            final String inputTopic = properties.getProperty("input.customer.topic");



            ProducerRecord<Integer, Customer> record = new ProducerRecord<>(inputTopic, customer.getCustomerId(), customer);
            producer.send(record, callback);
        }
    }
    public static void main(final String[] args) throws IOException {
//        TopicLoader.runProducer();
        logger.info("working");
    }
}
