package worker;

import model.Store;
import model.Product;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.JsonUtils;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import utils.DistanceUtils;

public class WorkerHandler implements Runnable {

    private Socket socket;
    private HashMap<String, Store> storeMap;

    public WorkerHandler(Socket socket, HashMap<String, Store> storeMap) {
        this.socket = socket;
        this.storeMap = storeMap;
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            writer.println("✅ Connected to Worker!");

            String input;
            while ((input = reader.readLine()) != null) {
                System.out.println("📥 Received from Master: " + input);

                //ADD_STORE_DATA:{json object string}

                if (input.startsWith("SEARCH_REQUEST:")) {
                    String json = input.substring("SEARCH_REQUEST:".length());
                    JSONObject obj = new JSONObject(json);
                    double lat = obj.getDouble("latitude");
                    double lng = obj.getDouble("longitude");
                    String category = obj.getString("category");
                    int stars = obj.getInt("stars");
                    String price = obj.getString("price");

                    // Filter stores by criteria
                    JSONArray matchedStores = new JSONArray();
                    for (Store store : storeMap.values()) {
                        if (store.getFoodCategory().equalsIgnoreCase(category) &&
                                store.getStars() >= stars &&
                                JsonUtils.calculatePriceCategory(store.getProducts()).equals(price) &&
                                DistanceUtils.distanceKm(lat, lng, store.getLatitude(), store.getLongitude()) <= 5.0) {

                            matchedStores.put(store.toJSON());
                        }
                    }

                    writer.println(matchedStores.toString());
                    writer.flush();
                }
                else if (input.startsWith("ADD_STORE_DATA:")) {

                    String json = input.substring("ADD_STORE_DATA:".length()).trim();
                    try {
                        JSONObject obj = new JSONObject(json);
                        Store store = Store.fromJSON(obj);
                        storeMap.put(store.getStoreName(), store);
                        writer.println("✅ Store added: " + store.getStoreName());
                    } catch (Exception e) {
                        writer.println("❌ Failed to parse JSON store object.");
                    }
                }

                else if (input.startsWith("ADD_STORE_JSON:")) {
                    String[] parts = input.split(":");
                    if (parts.length == 2) {
                        String jsonPath = parts[1];
                        Store store = JsonUtils.loadStoreFromJson(jsonPath);
                        if (store != null) {
                            synchronized (storeMap) {
                                storeMap.put(store.getStoreName(), store);
                            }
                            writer.println("✅ Store added: " + store.getStoreName());
                        } else {
                            writer.println("❌ Failed to load store.");
                        }
                    } else {
                        writer.println("❌ Invalid ADD_STORE_JSON format.");
                    }
                } else if (input.startsWith("ADD_PRODUCT:")) {
                    String[] parts = input.split(":", 3);
                    if (parts.length != 3) {
                        writer.println("❌ Format: ADD_PRODUCT:storeName:productData");
                        continue;
                    }

                    String storeName = parts[1];
                    String[] productParts = parts[2].split(",");

                    if (productParts.length != 4) {
                        writer.println("❌ Format: name,type,amount,price");
                        continue;
                    }

                    String name = productParts[0];
                    String type = productParts[1];
                    int amount = Integer.parseInt(productParts[2]);
                    double price = Double.parseDouble(productParts[3]);

                    Store store;
                    synchronized (storeMap) {
                        store = storeMap.get(storeName);
                    }
                    if (store == null) {
                        writer.println("❌ Το κατάστημα δεν βρέθηκε.");
                        continue;
                    }
                    synchronized (store) {
                        store.getProducts().add(new model.Product(name, type, amount, price));
                    }
                    writer.println("✅ Προστέθηκε προϊόν στο κατάστημα " + storeName);

                } else if (input.startsWith("REMOVE_PRODUCT:")) {
                    String[] parts = input.split(":", 3);
                    if (parts.length != 3) {
                        writer.println("Format: REMOVE_PRODUCT:store:product");
                        continue;
                    }

                    String storeName = parts[1];
                    String productName = parts[2];

                    Store store;
                    synchronized (storeMap) {
                        store = storeMap.get(storeName);
                    }
                    if (store == null) {
                        writer.println("❌ Το κατάστημα δεν βρέθηκε.");
                        continue;
                    }
                    boolean removed;
                    synchronized (store) {
                        removed = store.getProducts().removeIf(p -> p.getProductName().equalsIgnoreCase(productName));
                    }
                    if (removed) {
                        writer.println("Το προϊόν αφαιρέθηκε επιτυχώς.");
                    } else {
                        writer.println("Το προϊόν δεν βρέθηκε στο κατάστημα.");
                    }
                } else if (input.startsWith("UPDATE_STOCK:")) {
                    String[] parts = input.split(":", 4);
                    if (parts.length != 4) {
                        writer.println("Format: UPDATE_STOCK:store:product:amount");
                        continue;
                    }

                    String storeName = parts[1];
                    String productName = parts[2];
                    int amount = Integer.parseInt(parts[3]);

                    Store store;
                    synchronized (storeMap) {
                        store = storeMap.get(storeName);
                    }
                    if (store == null) {
                        writer.println("❌ Το κατάστημα δεν βρέθηκε.");
                        continue;
                    }
                    boolean updated = false;
                    synchronized (store) {
                        for (model.Product product : store.getProducts()) {
                            if (product.getProductName().equalsIgnoreCase(productName)) {
                                product.setAvailableAmount(amount);
                                updated = true;
                                break;
                            }
                        }
                    }

                    if (updated) {
                        writer.println("Το απόθεμα ενημερώθηκε.");
                    } else {
                        writer.println("Το προϊόν δεν βρέθηκε.");
                    }
                } else if (input.startsWith("QUERY_SALES_BY_CATEGORY:")) {
                    String targetCategory = input.split(":", 2)[1].trim();
                    for (Store store : storeMap.values()) {
                        if (store.getFoodCategory().equalsIgnoreCase(targetCategory)) {
                            writer.println(store.getStoreName() + " → " + store.getTotalSales() + " πωλήσεις, €" + store.getTotalRevenue());
                        }
                    }
                    writer.println("END");
                } else if (input.startsWith("QUERY_SALES_BY_PRODUCT:")) {
                    String productCategory = input.split(":", 2)[1].trim();
                    for (Store store : storeMap.values()) {
                        for (model.Product p : store.getProducts()) {
                            if (p.getProductType().equalsIgnoreCase(productCategory)) {
                                writer.println(store.getStoreName() + " → " + p.getProductName() + ": διαθέσιμα " + p.getAvailableAmount());
                            }
                        }
                    }
                    writer.println("END");
                } else if (input.startsWith("SEARCH:")) {
                    String[] parts = input.split(":", 2);
                    if (parts.length != 2) {
                        writer.println("Format: SEARCH:category,min,max,$,dist,lat,lon");
                        writer.println("END");
                        return;
                    }

                    String[] filters = parts[1].split(",");
                    if (filters.length != 7) {
                        writer.println("Λάθος αριθμός παραμέτρων.");
                        writer.println("END");
                        return;
                    }

                    String category = filters[0];
                    int minStars = Integer.parseInt(filters[1]);
                    int maxStars = Integer.parseInt(filters[2]);
                    String priceCategory = filters[3];
                    double maxDistance = Double.parseDouble(filters[4]);
                    double clientLat = Double.parseDouble(filters[5]);
                    double clientLon = Double.parseDouble(filters[6]);

                    for (Store store : storeMap.values()) {
                        if (!store.getFoodCategory().equalsIgnoreCase(category)) continue;
                        if (store.getStars() < minStars || store.getStars() > maxStars) continue;
                        if (!store.getPriceCategory().equalsIgnoreCase(priceCategory)) continue;

                        double dist = utils.DistanceUtils.distanceKm(clientLat, clientLon, store.getLatitude(), store.getLongitude());
                        if (dist > maxDistance) continue;

                        writer.println(store.getStoreName() + " [" + category + "] ⭐" + store.getStars() +
                                " | Τιμή: " + store.getPriceCategory() + " | Απόσταση: " + String.format("%.2f", dist) + " km");
                    }

                    writer.println("END");
                } else if (input.startsWith("BUY:")) {
                    String[] parts = input.split(":", 4);
                    if (parts.length != 4) {
                        writer.println("Format: BUY:store:product:amount");
                        return;
                    }

                    String storeName = parts[1];
                    String productName = parts[2];
                    int quantity = Integer.parseInt(parts[3]);

                    Store store;
                    synchronized (storeMap) {
                        store = storeMap.get(storeName);
                    }
                    if (store == null) {
                        writer.println("❌ Το κατάστημα δεν βρέθηκε.");
                        return;
                    }

                    synchronized (store) {
                        for (model.Product p : store.getProducts()) {
                            if (p.getProductName().equalsIgnoreCase(productName)) {
                                if (p.getAvailableAmount() < quantity) {
                                    writer.println("❌ Μη επαρκές απόθεμα.");
                                    return;
                                }
                                p.setAvailableAmount(p.getAvailableAmount() - quantity);
                                store.setTotalSales(store.getTotalSales() + quantity);
                                store.setTotalRevenue(store.getTotalRevenue() + quantity * p.getPrice());
                                writer.println("✅ Η αγορά ολοκληρώθηκε επιτυχώς.");
                                return;
                            }
                        }
                        writer.println("❌ Το προϊόν δεν βρέθηκε.");
                    }

                    writer.println("⚠️ Unknown command.");
                }

            }

        } catch (IOException e) {
            System.err.println("WorkerHandler Error: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // τιποτα
            }
        }
    }
}