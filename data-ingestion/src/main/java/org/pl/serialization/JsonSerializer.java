package org.pl.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

/**
 * Class to serialize the given object of Class type 'T'
 * @param <T> Parameter of class type 'T'
 */
public class JsonSerializer<T> implements Serializer<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Constructs JsonSerializer object and creates the ObjectMapper
     * and used to serialize the given parameter data
     */
    public JsonSerializer() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public byte[] serialize(final String topic, final T data) {
        if (data == null) {
            return new byte[0];
        }

        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }
}
