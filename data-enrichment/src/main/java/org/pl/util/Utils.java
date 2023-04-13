package org.pl.util;

import java.io.FileInputStream;
import java.util.Properties;

public class Utils {
    public static Properties loadProperties() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("data-enrichment\\src\\main\\resources\\application.properties")) {
            properties.load(fis);
            return properties;
        }
        catch (Exception e){
            System.out.println("Properties not loaded");
            return properties;
        }
    }


    public static String getCustomerSegment(Integer age){
        if(age < 18){
            return "Under 18";
        } else if(age < 25){
            return "18-24 years";
        } else if (age < 35) {
            return "25-34 years";
        } else if (age < 45) {
            return "35-44 years";
        } else {
            return "45+ years";
        }
    }
}