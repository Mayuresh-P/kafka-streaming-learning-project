package org.pl.util;

import lombok.Data;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Data
public class ApplicationProperties {

    @ConfigProperty(name = "input.sales.topic")
    String kafkaStreamsTopicSource;

    @ConfigProperty(name = "input.customer.topic")
    String customerTopic;

    @ConfigProperty(name = "TOPIC_SINK")
    String topicSink;

    @ConfigProperty(name = "quarkus.kafka-streams.bootstrap-servers")
    String bootstrapServers;

    @ConfigProperty(name = "TOPIC_TOTAL_SALES")
    String totalSalesTopic;

}
