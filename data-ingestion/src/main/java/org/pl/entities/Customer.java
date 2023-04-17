package org.pl.entities;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Customer   {

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

}
