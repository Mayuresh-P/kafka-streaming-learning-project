package org.pl.serializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * Class to deserialize the given object of Class type 'T'
 * @param <T> Parameter of class type 'T'
 */
public class JsonDeserializer<T> implements Deserializer<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> tClass;

    /**
     * Constructs JsonDeserializer object by setting the class level variables
     * using given parameter tClass i.e., Class to be deserializer
     * @param tClass class of type 'tClass'
     */
    public JsonDeserializer(Class<T> tClass) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        this.tClass = tClass;
    }

    @Override
    public void configure(final Map<String, ?> props, final boolean isKey) {
        //not needed, everything needed for init is done in the cnstr
    }

    @Override
    public T deserialize(final String topic, final byte[] data) {
        if (data == null) {
            return null;
        }

        T object;
        try {
            object = objectMapper.readValue(data, tClass);
        } catch (final Exception e) {
            throw new SerializationException(e.getMessage(), e);
        }
        return object;
    }
}