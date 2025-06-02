package worker;

import model.Store;
import utils.JsonUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class WorkerServer {

    private static HashMap<String, Store> storeMap = new HashMap<>();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("❌ Χρήση: java WorkerServer <port>");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("❌ Άκυρος αριθμός port.");
            return;
        }

        System.out.println("🔧 Worker ξεκινάει στη θύρα " + port + "...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket masterSocket = serverSocket.accept();
                System.out.println("📡 Νέα σύνδεση από Master: " + masterSocket.getInetAddress());

                WorkerHandler handler = new WorkerHandler(masterSocket, storeMap);
                Thread thread = new Thread(handler);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("❌ Σφάλμα Worker: " + e.getMessage());
        }
    }
}