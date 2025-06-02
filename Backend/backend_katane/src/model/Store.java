package model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Store {
    private String storeName;
    private double latitude;
    private double longitude;
    private String foodCategory;
    private int stars;
    private int noOfVotes;
    private String storeLogoPath;
    private List<Product> products;
    private String priceCategory; // "$", "$$", "$$$"

    private int totalSales;
    private double totalRevenue;

    public Store(String storeName, double latitude, double longitude, String foodCategory,
                 int stars, int noOfVotes, String storeLogoPath, List<Product> products) {
        this.storeName = storeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.foodCategory = foodCategory;
        this.stars = stars;
        this.noOfVotes = noOfVotes;
        this.storeLogoPath = storeLogoPath;
        this.products = products;
        this.priceCategory = calculatePriceCategory();
        this.totalSales = 0;
        this.totalRevenue = 0.0;
    }

    // Store from JSONObject
    public static Store fromJSON(JSONObject obj) {
        String storeName = obj.getString("storeName");
        double latitude = obj.getDouble("latitude");
        double longitude = obj.getDouble("longitude");
        String foodCategory = obj.getString("foodCategory");
        int stars = obj.getInt("stars");
        int noOfVotes = obj.has("noOfVotes") ? obj.getInt("noOfVotes") : 0;
        String storeLogoPath = obj.has("storeLogoPath") ? obj.getString("storeLogoPath") : "";

        List<Product> products = new ArrayList<>();
        JSONArray productsArray = obj.getJSONArray("products");

        for (int i = 0; i < productsArray.length(); i++) {
            JSONObject prod = productsArray.getJSONObject(i);
            String name = prod.getString("productName");
            String type = prod.getString("productType");
            int amount = prod.getInt("availableAmount");
            double price = prod.getDouble("price");

            products.add(new Product(name, type, amount, price));
        }

        return new Store(storeName, latitude, longitude, foodCategory, stars, noOfVotes, storeLogoPath, products);
    }

    private String calculatePriceCategory() {
        double sum = 0;
        for (Product product : products) {
            sum += product.getPrice();
        }
        double average = sum / products.size();
        if (average <= 5) return "$";
        else if (average <= 15) return "$$";
        else return "$$$";
    }

    public String getStoreName() {
        return storeName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getFoodCategory() {
        return foodCategory;
    }

    public int getStars() {
        return stars;
    }

    public int getNoOfVotes() {
        return noOfVotes;
    }

    public String getStoreLogoPath() {
        return storeLogoPath;
    }

    public List<Product> getProducts() {
        return products;
    }

    public String getPriceCategory() {
        return priceCategory;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public void addSales(int quantity, double revenue) {
        this.totalSales += quantity;
        this.totalRevenue += revenue;
    }

    @Override
    public String toString() {
        return storeName + " [" + foodCategory + "] " + stars + "★ - " + priceCategory;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("storeName", storeName);
        obj.put("latitude", latitude);
        obj.put("longitude", longitude);
        obj.put("foodCategory", foodCategory);
        obj.put("stars", stars);
        obj.put("price", priceCategory); // αν υπάρχει
        JSONArray productsArr = new JSONArray();
        for (Product p : products) {
            productsArr.put(p.toJSON());
        }
        obj.put("products", productsArr);
        return obj;
    }
}