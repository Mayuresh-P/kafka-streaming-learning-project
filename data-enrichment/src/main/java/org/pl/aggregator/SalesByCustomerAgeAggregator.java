package org.pl.aggregator;

import org.apache.kafka.streams.kstream.Aggregator;
import org.pl.entities.CustomerSales;
import org.pl.entities.SalesByCustomerAge;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SalesByCustomerAgeAggregator implements Aggregator<String, CustomerSales, SalesByCustomerAge> {
    @Override
    public SalesByCustomerAge apply(String s, CustomerSales customerSales, SalesByCustomerAge salesByCustomerAge) {
        SalesByCustomerAge newSalesByCustomerAge = new SalesByCustomerAge();
        newSalesByCustomerAge.setTotalSales(salesByCustomerAge.getTotalSales()+(customerSales.getQuantity()*customerSales.getPrice()));
        newSalesByCustomerAge.setAgeSegment(s);
        newSalesByCustomerAge.setSalesCount(salesByCustomerAge.getSalesCount()+1);
        newSalesByCustomerAge.setAverageSales((salesByCustomerAge.getTotalSales()+(customerSales.getQuantity()*customerSales.getPrice()))/(salesByCustomerAge.getSalesCount()+1));
        return newSalesByCustomerAge;
    }
}
