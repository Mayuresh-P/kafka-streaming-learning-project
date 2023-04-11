package org.pl.serde;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class JSONSerde implements Serializer<JsonNode>, Deserializer<JsonNode> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public JsonNode deserialize(String s, byte[] bytes) {
        JsonNode deserialized = null;
        try{
            deserialized = objectMapper.readValue(bytes, JsonNode.class);

        } catch (Exception e){
            e.printStackTrace();
        }
        return deserialized;
    }

    @Override
    public JsonNode deserialize(String topic, Headers headers, byte[] data) {
        return Deserializer.super.deserialize(topic, headers, data);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String s, JsonNode jsonNode) {
        if (jsonNode == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsBytes(jsonNode);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error serializing JSON message", e);
        }
    }

    @Override
    public byte[] serialize(String topic, Headers headers, JsonNode data) {
        return Serializer.super.serialize(topic, headers, data);
    }

    @Override
    public void close() {
        Serializer.super.close();
    }

}
