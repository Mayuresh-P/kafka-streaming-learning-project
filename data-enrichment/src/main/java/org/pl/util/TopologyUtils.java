package org.pl.util;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.pl.serde.CustomSerde;

public class TopologyUtils {

    public static <T> Materialized<String, T, KeyValueStore<Bytes, byte[]>> materialize(Class<T> tClass, String storeName, boolean loggingDisabled) {

        Materialized<String, T, KeyValueStore<Bytes, byte[]>> materialized = Materialized.<String, T, KeyValueStore<Bytes, byte[]>>as(storeName).withKeySerde(Serdes.String()).withValueSerde(new CustomSerde<>(tClass).generateSerde());

        if (loggingDisabled) {

            materialized = materialized.withLoggingDisabled();

        }

        return materialized;
    }

}