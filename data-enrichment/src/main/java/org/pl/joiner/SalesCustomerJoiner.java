package org.pl.joiner;

import org.apache.kafka.streams.kstream.ValueJoiner;
import org.pl.entities.Customer;
import org.pl.entities.CustomerSales;
import org.pl.entities.Sales;

public class SalesCustomerJoiner implements ValueJoiner<Sales, Customer, CustomerSales> {
    @Override
    public CustomerSales apply(Sales sales, Customer customer) {
        CustomerSales updatedCustomerSales = new CustomerSales();
        updatedCustomerSales.setSalesId(sales.getSalesId());
        updatedCustomerSales.setAge(customer.getAge());
        updatedCustomerSales.setGender(customer.getGender());
        updatedCustomerSales.setCustomerId(customer.getCustomerId());
        updatedCustomerSales.setName(customer.getName());
        updatedCustomerSales.setProductCategory(sales.getProductCategory());
        updatedCustomerSales.setTimestamp(sales.getTimestamp());
        updatedCustomerSales.setQuantity(sales.getQuantity());
        updatedCustomerSales.setPrice(sales.getPrice());
        updatedCustomerSales.setProductId(sales.getProductId());
        return updatedCustomerSales;
    }
}
