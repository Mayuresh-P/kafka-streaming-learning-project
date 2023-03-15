package org.pl.streams;
import io.quarkus.kafka.client.serialization.ObjectMapperSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.pl.entities.Customer;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Produces;

@ApplicationScoped
public class TopologyProducer {

    private static final String CUSTOMER_TOPIC = "customer-data";
//    private static final String

    @Produces
    public Topology buildTopology() {
        final StreamsBuilder builder = new StreamsBuilder();

        final ObjectMapperSerde<Customer> customerSerde = new ObjectMapperSerde<>(Customer.class);
        final KStream<Integer, Customer> firstStream = builder.stream(CUSTOMER_TOPIC, Consumed.with(Serdes.Integer(), customerSerde));
        firstStream.peek((key, value) -> System.out.println("key "+key+" value "+value));

        return builder.build();
    }
}
