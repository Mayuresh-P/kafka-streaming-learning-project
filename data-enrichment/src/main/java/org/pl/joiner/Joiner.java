package org.pl.joiner;

import org.apache.kafka.streams.kstream.ValueJoiner;
import org.pl.entities.AllData;
import org.pl.entities.Customer;
import org.pl.entities.Sales;
import org.pl.entities.TotalSales;

public class Joiner implements ValueJoiner<Customer, Sales, AllData> {

    @Override
    public AllData apply(Customer customer, Sales sales) {
        return null;
    }

    public AllData joinAllData(Customer customer, Sales sales) {

        AllData data = new AllData();

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

    public TotalSales joinTotalSales(AllData allData, TotalSales totalSales) {

        TotalSales sales = new TotalSales();

        sales.setTotalSales(totalSales.getTotalSales() + allData.getPrice());
        sales.setProductCategory(allData.getProductCategory());

        return sales;
    }

}
