package org.pl.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.io.Serializable;
import java.util.Map;

public class Customer implements Serializable, Serializer {

    @JsonProperty("id")
    private long customerId;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("age")
    private int age;

    @JsonProperty("name")
    private String name;

    public Customer(int customerId, String gender, int age, String name) {
        this.customerId = customerId;
        this.gender = gender;
        this.age = age;
        this.name = name;
    }

    public Customer() {

    }

    public void configure(Map configs, boolean isKey) {

    }

    public byte[] serialize(String s, Object o) {
        return new byte[0];
    }

    public byte[] serialize(String topic, Headers headers, Object data) {
        return new byte[0];
    }

    public void close() {

    }
}
