package org.pl.utils;

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
}
