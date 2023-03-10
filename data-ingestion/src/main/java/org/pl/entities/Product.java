package org.pl.entities;

public class Product {

    private int productId;

    private String productName;

    private String productCategory;

    private long price;

    public Product(int productId, String productName, String productCategory, long price) {
        this.productId = productId;
        this.productName = productName;
        this.productCategory = productCategory;
        this.price = price;
    }
}
