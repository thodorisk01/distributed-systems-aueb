package utils;

import model.Product;
import model.Store;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonUtils {

    // load one store from JSONObject
    public static Store loadStoreFromObject(JSONObject object) {
        String storeName = object.getString("storeName");
        double latitude = object.getDouble("latitude");
        double longitude = object.getDouble("longitude");
        String foodCategory = object.getString("foodCategory");
        int stars = object.getInt("stars");

        int noOfVotes = object.has("noOfVotes") ? object.getInt("noOfVotes") : 0;
        String storeLogoPath = object.has("storeLogoPath") ? object.getString("storeLogoPath") : "";

        JSONArray productsArray = object.getJSONArray("products");
        List<Product> products = new ArrayList<>();

        for (int i = 0; i < productsArray.length(); i++) {
            JSONObject prodObj = productsArray.getJSONObject(i);
            String productName = prodObj.getString("productName");
            String productType = prodObj.getString("productType");
            int availableAmount = prodObj.getInt("availableAmount");
            double price = prodObj.getDouble("price");

            products.add(new Product(productName, productType, availableAmount, price));
        }

        return new Store(storeName, latitude, longitude, foodCategory, stars, noOfVotes, storeLogoPath, products);
    }

    // load one store from JSON file (JSONObject format)
    public static Store loadStoreFromJson(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject object = new JSONObject(tokener);
            return loadStoreFromObject(object);
        } catch (Exception e) {
            System.err.println("❌ Σφάλμα κατά την ανάγνωση του αρχείου JSON: " + filePath);
            e.printStackTrace();
            return null;
        }
    }

    // load a lot stores from JSON files (JSONArray format)
    public static List<Store> loadStoresFromJsonArray(String filePath) {
        List<Store> stores = new ArrayList<>();
        try (FileReader reader = new FileReader(filePath)) {
            JSONArray arr = new JSONArray(new JSONTokener(reader));
            for (int i = 0; i < arr.length(); i++) {
                stores.add(loadStoreFromObject(arr.getJSONObject(i)));
            }
        } catch (Exception e) {
            System.err.println("❌ Σφάλμα κατά την ανάγνωση καταστημάτων από array JSON: " + filePath);
            e.printStackTrace();
        }
        return stores;
    }

    public static String calculatePriceCategory(List<Product> products) {
        double total = 0;
        for (Product p : products) {
            total += p.getPrice();
        }
        double avg = total / products.size();

        if (avg < 5) return "$";
        else if (avg <= 10) return "$$";
        else return "$$$";
    }

}