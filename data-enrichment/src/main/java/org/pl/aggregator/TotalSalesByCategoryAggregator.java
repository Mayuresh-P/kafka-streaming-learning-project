package org.pl.aggregator;

import org.apache.kafka.streams.kstream.Aggregator;
import org.pl.entities.CustomerSales;
import org.pl.entities.TotalSalesByCategory;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TotalSalesByCategoryAggregator implements Aggregator<String, CustomerSales, TotalSalesByCategory> {
    @Override
    public TotalSalesByCategory apply(String s, CustomerSales customerSales, TotalSalesByCategory totalSalesByCategory) {
        System.out.println(customerSales.getPrice()+" "+customerSales.getQuantity()+" "+totalSalesByCategory.getTotalSales());
        return new TotalSalesByCategory(s, totalSalesByCategory.getTotalSales()+(customerSales.getPrice()*customerSales.getQuantity()));
    }
}
