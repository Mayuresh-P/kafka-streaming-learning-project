package org.pl.aggregator;

import org.apache.kafka.streams.kstream.Aggregator;
import org.pl.entities.CustomerSales;
import org.pl.entities.TotalSalesByCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TotalSalesByCategoryAggregator implements Aggregator<String, CustomerSales, TotalSalesByCategory> {

    private static final Logger logger = LoggerFactory.getLogger(TotalSalesByCategoryAggregator.class.getSimpleName());

    @Override
    public TotalSalesByCategory apply(String s, CustomerSales customerSales, TotalSalesByCategory totalSalesByCategory) {

        logger.info(customerSales.getPrice()+" "+customerSales.getQuantity()+" "+totalSalesByCategory.getTotalSales());

        return new TotalSalesByCategory(s, totalSalesByCategory.getTotalSales()+(customerSales.getPrice()*customerSales.getQuantity()));

    }

}
