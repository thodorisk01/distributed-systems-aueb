package manager;

import java.io.*;
import java.net.Socket;

public class ManagerTestClient {
    public static void main(String[] args) {
        String workerHost = "localhost";
        int workerPort = 5001;  // port worker

        try (Socket socket = new Socket(workerHost, workerPort);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connected to Worker!");

            // read workers first response
            System.out.println("Worker: " + reader.readLine());

            // for json load
            writer.println("ADD_STORE_JSON:data/stores.json");

            // print answer
            String response;
            while ((response = reader.readLine()) != null) {
                System.out.println("Worker Response: " + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
