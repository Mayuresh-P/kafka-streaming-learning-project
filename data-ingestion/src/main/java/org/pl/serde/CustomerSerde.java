package org.pl.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.pl.entities.Customer;

public class CustomerSerde implements Serde<Customer> {
    @Override
    public Serializer<Customer> serializer() {
        return new Customer();
    }

    @Override
    public Deserializer<Customer> deserializer() {
        return new Customer();
    }
}
