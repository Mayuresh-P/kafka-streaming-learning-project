package org.pl.entities;

public class Customer {

    private int customerId;

    private String gender;

    private int age;

    private String name;

    public Customer(int customerId, String gender, int age, String name) {
        this.customerId = customerId;
        this.gender = gender;
        this.age = age;
        this.name = name;
    }

}
