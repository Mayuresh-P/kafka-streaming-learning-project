package org.pl.serde;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.pl.serializer.JsonDeserializer;
import org.pl.serializer.JsonSerializer;

import java.util.Map;

public class JSONSerde<T> implements  Serde<T> {

    private final Deserializer<T> deserializer;
    private final Serializer<T> serializer;


    private final ObjectMapper objectMapper = new ObjectMapper();

    public JSONSerde(Class<T> tClass) {
        this.deserializer = new JsonDeserializer<>(tClass);
        this.serializer = new JsonSerializer<>();
    }


    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        this.serializer.configure(configs, isKey);
        this.deserializer.configure(configs, isKey);
    }



    @Override
    public void close() {
        this.serializer.close();
        this.deserializer.close();
    }

    @Override
    public Serializer<T> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<T> deserializer() {
        return deserializer;
    }
}
