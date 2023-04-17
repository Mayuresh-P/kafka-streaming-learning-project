package org.pl.aggregator;

import org.apache.kafka.streams.kstream.Aggregator;
import org.pl.entities.CustomerSales;
import org.pl.entities.SalesByCustomerAge;
import org.pl.entities.SalesByCustomerGender;

public class SalesByCustomerGenderAggregator implements Aggregator<String, CustomerSales, SalesByCustomerGender> {
    @Override
    public SalesByCustomerGender apply(String s, CustomerSales customerSales, SalesByCustomerGender salesByCustomerGender) {
//        SalesByCustomerGender newSalesByCustomerGender = new SalesByCustomerGender();
//        newSalesByCustomerGender.setCustomerGender(s);
//        newSalesByCustomerGender.setTotalSales(salesByCustomerGender.getTotalSales()+(customerSales.getPrice()*customerSales.getQuantity()));
//        newSalesByCustomerGender.setAverageSales((salesByCustomerGender.getTotalSales()+(customerSales.getQuantity()*customerSales.getPrice()))/(salesByCustomerGender.getSalesCount()+1));
//        newSalesByCustomerGender.setSalesCount(salesByCustomerGender.getSalesCount()+1);
//        newSalesByCustomerGender.addCategoryWiseSales(customerSales.getProductCategory().toLowerCase(), (customerSales.getPrice()*customerSales.getQuantity()));
        return salesByCustomerGender.aggregate(s, (customerSales.getPrice()*customerSales.getQuantity()), customerSales.getProductCategory().toLowerCase());
    }
}
