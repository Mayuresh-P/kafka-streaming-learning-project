package org.pl.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.pl.serialization.JsonDeserializer;
import org.pl.serialization.JsonSerializer;

public class CustomSerde<T> {

    private final Class<T> t;



    public CustomSerde(Class<T> t) {
        this.t = t;
    }

    public Serde<T> generateSerde(){
        Serializer<T> tSerializer = new JsonSerializer<>();
        Deserializer<T> tDeserializer = new JsonDeserializer<>(t);
        return Serdes.serdeFrom(tSerializer, tDeserializer);
    }

}
