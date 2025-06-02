package master;

import model.WorkerInfo;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MasterServer {

    public static final int PORT = 5000;
    private static List<WorkerInfo> workers = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("🧠 Master Server starting on port " + PORT + "...");

        // ✅ load workers from config file
        workers = loadWorkersFromConfig("configs/workers.config");

        if (workers.isEmpty()) {
            System.err.println("❌ Δεν βρέθηκαν διαθέσιμοι Workers στο config.");
            return;
        }

        System.out.println("✅ Βρέθηκαν " + workers.size() + " Workers.");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("🔌 Νέα σύνδεση: " + clientSocket.getInetAddress());

                Thread handler = new Thread(new MasterClientHandler(clientSocket, workers));
                handler.start();
            }
        } catch (IOException e) {
            System.err.println("❌ Σφάλμα στο MasterServer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<WorkerInfo> loadWorkersFromConfig(String filePath) {
        List<WorkerInfo> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 2) {
                    String host = parts[0];
                    int port = Integer.parseInt(parts[1]);
                    list.add(new WorkerInfo(host, port));
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Σφάλμα ανάγνωσης αρχείου config: " + e.getMessage());
        }
        return list;
    }
}