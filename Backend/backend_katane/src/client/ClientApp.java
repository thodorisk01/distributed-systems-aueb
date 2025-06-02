package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.Locale;

public class ClientApp {

    public static void main(String[] args) {
        String masterHost = "localhost";
        int masterPort = 5000;

        try (Socket socket = new Socket(masterHost, masterPort);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("📡 Συνδέθηκες με τον Master!");
            System.out.println("📥 " + reader.readLine());

            while (true) {
                System.out.println("\n===== MENU CLIENT =====");
                System.out.println("1. Αναζήτηση καταστημάτων");
                System.out.println("2. Αγορά προϊόντος");
                System.out.println("3. Έξοδος");
                System.out.print("Επιλογή: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                if (choice == 1) {
                    System.out.print("🍽️ Κατηγορία φαγητού: ");
                    String foodCategory = scanner.nextLine();

                    System.out.print("⭐ Ελάχιστα αστέρια: ");
                    int minStars = Integer.parseInt(scanner.nextLine().trim());

                    System.out.print("⭐ Μέγιστα αστέρια: ");
                    int maxStars = Integer.parseInt(scanner.nextLine().trim());

                    System.out.print("💰 Κατηγορία τιμής ($/$$/$$$): ");
                    String priceCategory = scanner.nextLine().trim();

                    System.out.print("📏 Μέγιστη απόσταση (km): ");
                    String distInput = scanner.nextLine().trim().replace(",", ".");
                    if (distInput.isEmpty()) {
                        System.out.println("❌ Η απόσταση δεν μπορεί να είναι κενή.");
                        continue;
                    }
                    double maxDistance = Double.parseDouble(distInput);

                    System.out.print("🧭 Τοποθεσία σας (latitude): ");
                    String latInput = scanner.nextLine().trim().replace(",", ".");
                    if (latInput.isEmpty()) {
                        System.out.println("❌ Το latitude δεν μπορεί να είναι κενό.");
                        continue;
                    }
                    double lat = Double.parseDouble(latInput);

                    System.out.print("🧭 Τοποθεσία σας (longitude): ");
                    String lonInput = scanner.nextLine().trim().replace(",", ".");
                    if (lonInput.isEmpty()) {
                        System.out.println("❌ Το longitude δεν μπορεί να είναι κενό.");
                        continue;
                    }
                    double lon = Double.parseDouble(lonInput);

                    // Format with dots
                    String command = String.format(Locale.US,
                            "SEARCH:%s,%d,%d,%s,%.2f,%.6f,%.6f",
                            foodCategory, minStars, maxStars, priceCategory,
                            maxDistance, lat, lon);

                    System.out.println("📤 Αποστολή στον Master: " + command);
                    writer.println(command);

                    System.out.println("⏳ Αναμονή αποτελεσμάτων...");
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.equals("END")) break;
                        System.out.println("• " + line);
                    }

                } else if (choice == 2) {
                    System.out.print("🏪 Κατάστημα: ");
                    String store = scanner.nextLine();
                    System.out.print("🍕 Προϊόν: ");
                    String product = scanner.nextLine();
                    System.out.print("🔢 Ποσότητα: ");
                    int qty = scanner.nextInt();
                    scanner.nextLine(); // clear newline

                    String command = String.format("BUY:%s:%s:%d", store, product, qty);
                    writer.println(command);

                    String response = reader.readLine();
                    System.out.println("📦 Αποτέλεσμα: " + response);

                } else if (choice == 3) {
                    writer.println("exit");
                    System.out.println("👋 Έξοδος από την εφαρμογή.");
                    break;
                } else {
                    System.out.println("❌ Μη έγκυρη επιλογή.");
                }
            }

        } catch (IOException e) {
            System.err.println("❌ Σφάλμα Client: " + e.getMessage());
        }
    }
}