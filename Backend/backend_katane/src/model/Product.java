package model;

import org.json.JSONObject;

public class Product {
    private String productName;
    private String productType;
    private int availableAmount;
    private double price;

    public Product(String productName, String productType, int availableAmount, double price) {
        this.productName = productName;
        this.productType = productType;
        this.availableAmount = availableAmount;
        this.price = price;
    }

    // Getters and Setters
    public String getProductName() {
        return productName;
    }

    public String getProductType() {
        return productType;
    }

    public int getAvailableAmount() {
        return availableAmount;
    }

    public double getPrice() {
        return price;
    }

    public void setAvailableAmount(int availableAmount) {
        this.availableAmount = availableAmount;
    }

    @Override
    public String toString() {
        return productName + " (" + productType + ") - " + price + "€ (Διαθέσιμα: " + availableAmount + ")";
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("productName", productName);
        obj.put("productType", productType);
        obj.put("availableAmount", availableAmount);
        obj.put("price", price);
        return obj;
    }
}