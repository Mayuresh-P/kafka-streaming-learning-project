package org.pl.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.io.Serializable;
import java.util.Map;


public class Customer implements Serializable, Serializer, Deserializer {

    @JsonProperty("id")
    private int customerId;

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

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void configure(Map configs, boolean isKey) {

    }


    public byte[] serialize(String s, Object c) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsString(c).getBytes();
        } catch (Exception e){
            e.printStackTrace();
        }

        return retVal;
    }

//    @Override
//    public byte[] serialize(String s, Object o) {
//        return new byte[0];
//    }

    @Override
    public byte[] serialize(String topic, Headers headers, Object data) {
        return Serializer.super.serialize(topic, headers, data);
    }

    @Override
    public void close() {

    }

//    public byte[] serialize(String topic, Headers headers, Object data) {
//        return new byte[0];
//    }

    @Override
    public Customer deserialize(String s, byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper();
        Customer customer = null;
        try{
            customer = mapper.readValue(bytes, Customer.class);

        } catch (Exception e){
            e.printStackTrace();
        }
        return customer;
    }

    @Override
    public Object deserialize(String topic, Headers headers, byte[] data) {
        return Deserializer.super.deserialize(topic, headers, data);
    }


}
