package org.pl.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pl.entities.Sales;
import org.pl.serde.JSONSerde;

import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class SalesProducer {
    static ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LogManager.getLogger();
    //private static final String kafkaConfig = "/kafka.properties";
    static Callback callback = (metadata, exception) -> {
        if(exception != null) {
            System.out.printf("Producing records encountered error %s %n", exception);
        } else {
            System.out.printf("Record produced - offset - %d timestamp - %d %n", metadata.offset(), metadata.timestamp());
        }
    };
    public static void main(String[] args) throws IOException {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JSONSerde.class);

        KafkaProducer<Integer, JsonNode> kafkaProducer = new KafkaProducer<>(config);

        final String ordersTopic = "sales-data-topic";
        try (final AdminClient adminClient = AdminClient.create(config)) {
            if(!adminClient.listTopics(new ListTopicsOptions()).names().get().contains(ordersTopic)) {
                try {
                    NewTopic newTopic = new NewTopic(ordersTopic, 1, (short) 1);

                    final CreateTopicsResult createTopicsResult = adminClient.createTopics(Collections.singleton(newTopic));

                    createTopicsResult.values().get(ordersTopic).get();

                } catch (InterruptedException | ExecutionException e) {
                    if (!(e.getCause() instanceof TopicExistsException))
                        throw new RuntimeException(e.getMessage(), e);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            if (!(e.getCause() instanceof TopicExistsException))
                throw new RuntimeException(e.getMessage(), e);
        }

        String fileName = "data-ingestion\\src\\main\\resources\\SalesData.csv";

        List<Sales> datalist = new CsvToBeanBuilder(new FileReader(fileName))
                .withType(Sales.class)
                .build()
                .parse();

        for(Sales s:datalist){
            JsonNode jsonNode = objectMapper.convertValue(s,JsonNode.class);
            ProducerRecord<Integer,JsonNode> rec= new ProducerRecord<>(ordersTopic,s.getOrderId(),jsonNode);
            System.out.println(rec);

            kafkaProducer.send(rec,callback);

        }
        kafkaProducer.close();
    }
}