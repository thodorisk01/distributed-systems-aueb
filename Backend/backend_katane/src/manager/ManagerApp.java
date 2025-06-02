package manager;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.Locale;

public class ManagerApp {

    public static void main(String[] args) {
        final String masterHost = "localhost";
        final int masterPort = 5000;

        try (Socket socket = new Socket(masterHost, masterPort);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("✅ Συνδέθηκες με τον Master!");
            System.out.println("📥 " + reader.readLine());

            boolean running = true;
            while (running) {
                System.out.println("\n===== MENU MANAGER =====");
                System.out.println("1. Προσθήκη καταστήματος από JSON");
                System.out.println("2. Προσθήκη προϊόντος σε κατάστημα");
                System.out.println("3. Αφαίρεση προϊόντος από κατάστημα");
                System.out.println("4. Ενημέρωση αποθέματος προϊόντος");
                System.out.println("5. Πωλήσεις ανά FoodCategory");
                System.out.println("6. Πωλήσεις ανά ProductCategory");
                System.out.println("7. Έξοδος");
                System.out.print("Επιλογή: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        System.out.print("📁 Δώσε path JSON (π.χ. data/pizza1.json): ");
                        String path = scanner.nextLine();
                        writer.println("ADD_STORE_JSON:" + path);
                        System.out.println("⏳ Αναμονή απόκρισης...");
                        printResponse(reader);
                        break;

                    case 2:
                        System.out.print("🏪 Όνομα καταστήματος: ");
                        String storeName = scanner.nextLine();

                        System.out.print("🍕 Όνομα προϊόντος: ");
                        String productName = scanner.nextLine();

                        System.out.print("📂 Τύπος προϊόντος (π.χ. pizza, salad): ");
                        String productType = scanner.nextLine();

                        System.out.print("📦 Διαθέσιμη ποσότητα: ");
                        int amount = Integer.parseInt(scanner.nextLine().trim());

                        System.out.print("💶 Τιμή: ");
                        String priceInput = scanner.nextLine().trim().replace(",", ".");
                        double price = Double.parseDouble(priceInput);

                        // Locale.US
                        String command = String.format(Locale.US, "ADD_PRODUCT:%s:%s,%s,%d,%.2f",
                                storeName, productName, productType, amount, price);
                        writer.println(command);

                        System.out.println("⏳ Αναμονή απόκρισης...");
                        String response2 = reader.readLine();
                        System.out.println("📩 Απάντηση: " + response2);
                        break;

                    case 3:
                        System.out.print("🏪 Όνομα καταστήματος: ");
                        String rStore = scanner.nextLine();

                        System.out.print("🍕 Όνομα προϊόντος προς αφαίρεση: ");
                        String rProduct = scanner.nextLine();

                        String rCommand = String.format("REMOVE_PRODUCT:%s:%s", rStore, rProduct);
                        writer.println(rCommand);

                        System.out.println("⏳ Αναμονή απόκρισης...");
                        printResponse(reader);
                        break;

                    case 4:
                        System.out.print("🏪 Όνομα καταστήματος: ");
                        String uStore = scanner.nextLine();

                        System.out.print("🍕 Όνομα προϊόντος: ");
                        String uProduct = scanner.nextLine();

                        System.out.print("🔢 Νέα διαθέσιμη ποσότητα: ");
                        int newAmount = scanner.nextInt();
                        scanner.nextLine();

                        String uCommand = String.format("UPDATE_STOCK:%s:%s:%d", uStore, uProduct, newAmount);
                        writer.println(uCommand);

                        System.out.println("⏳ Αναμονή απόκρισης...");
                        printResponse(reader);
                        break;

                    case 5:
                        System.out.print("📂 Τύπος καταστήματος (π.χ. pizzeria): ");
                        String fcat = scanner.nextLine();
                        writer.println("QUERY_SALES_BY_CATEGORY:" + fcat);

                        System.out.println("⏳ Αναμονή απόκρισης...");
                        printMultilineResponse(reader);
                        break;

                    case 6:
                        System.out.print("📂 Κατηγορία προϊόντος (π.χ. pizza, salad): ");
                        String pcat = scanner.nextLine();
                        writer.println("QUERY_SALES_BY_PRODUCT:" + pcat);

                        System.out.println("⏳ Αναμονή απόκρισης...");
                        printMultilineResponse(reader);
                        break;

                    case 7:
                        running = false;
                        writer.println("exit");
                        System.out.println("👋 Έξοδος από την εφαρμογή.");
                        break;

                    default:
                        System.out.println("❌ Μη έγκυρη επιλογή.");
                }
            }

        } catch (IOException e) {
            System.err.println("❌ Σφάλμα σύνδεσης με Master: " + e.getMessage());
        }
    }

    private static void printResponse(BufferedReader reader) throws IOException {
        String response = reader.readLine();
        if (response != null) {
            System.out.println("📩 Απάντηση: " + response);
        }
    }

    private static void printMultilineResponse(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.equals("END")) break;
            System.out.println("• " + line);
        }
    }
}