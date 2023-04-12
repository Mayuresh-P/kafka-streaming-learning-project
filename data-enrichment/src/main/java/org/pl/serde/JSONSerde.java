package org.pl.serde;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class JSONSerde<T> implements Serializer<T>, Deserializer<T>, Serde<T> {

    private final Class<T> t;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JSONSerde(Class<T> t) {
        this.t = t;
    }

    @Override
    public T deserialize(String s, byte[] bytes) {
        T deserialized = null;
        try{
             deserialized = objectMapper.readValue(bytes, t);

        } catch (Exception e){
            e.printStackTrace();
        }
//        return customer;
        return deserialized;
    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        return Deserializer.super.deserialize(topic, headers, data);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String s, T T) {
        if (T == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsBytes(T);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error serializing JSON message", e);
        }
    }

    @Override
    public byte[] serialize(String topic, Headers headers, T data) {
        return Serializer.super.serialize(topic, headers, data);
    }

    @Override
    public void close() {
        Serializer.super.close();
    }

    @Override
    public Serializer<T> serializer() {
        return new JSONSerde(t);
    }

    @Override
    public Deserializer<T> deserializer() {
        return new JSONSerde(t);
    }
}
