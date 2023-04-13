package org.pl.joiner;

import org.apache.kafka.streams.kstream.ValueJoiner;
import org.pl.entities.CustomerSales;
import org.pl.entities.Customer;
import org.pl.entities.Sales;

public class Joiner implements ValueJoiner<Customer, Sales, CustomerSales> {

    @Override
    public CustomerSales apply(Customer customer, Sales sales) {
        return null;
    }

    public CustomerSales joinAllData(Customer customer, Sales sales) {

        CustomerSales data = new CustomerSales();

        data.setCustomerId(customer.getCustomerId());
        data.setSalesId(sales.getSalesId());
        data.setAge(customer.getAge());
        data.setGender(customer.getGender());
        data.setName(customer.getName());
        data.setPrice(sales.getPrice());
        data.setQuantity(sales.getQuantity());
        data.setProductCategory(sales.getProductCategory());
        data.setProductId(sales.getProductId());
        data.setTimestamp(sales.getTimestamp());

        return data;

    }

//    public TotalSalesByCategory joinTotalSales(CustomerSales customerSales, TotalSalesByCategory totalSales) {
//
//        TotalSalesByCategory sales = new TotalSalesByCategory();
//
//        sales.setTotalSales(totalSales.getTotalSales() + customerSales.getPrice());
//        sales.setProductCategory(customerSales.getProductCategory());
//
//        return sales;
//    }

}
