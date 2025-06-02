package master;

import model.WorkerInfo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MasterClientHandler implements Runnable {

    private Socket socket;
    private List<WorkerInfo> workers;

    public MasterClientHandler(Socket socket, List<WorkerInfo> workers) {
        this.socket = socket;
        this.workers = workers;
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            writer.println("✅ Connected to Master!");

            String input;
            while ((input = reader.readLine()) != null) {
                System.out.println("📥 Received from client: " + input);


                if (input.contains("\\\"action\\\":") && input.contains("\\\"search\\\"")) {
                    System.out.println("🔍 Search request received!");

                    List<String> results = new ArrayList<>();
                    for (WorkerInfo worker : workers) {
                        try {
                            Socket workerSocket = new Socket("127.0.0.1", worker.getPort());
                            PrintWriter outWorker = new PrintWriter(workerSocket.getOutputStream(), true);
                            BufferedReader inWorker = new BufferedReader(new InputStreamReader(workerSocket.getInputStream()));

                            outWorker.println("SEARCH_REQUEST:" + input);
                            String response = inWorker.readLine();
                            if (response != null && !response.isEmpty()) {
                                results.add(response);
                            }

                            workerSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    String finalResponse = "[" + String.join(",", results) + "]";
                    writer.println(finalResponse);
                    writer.flush();
                    System.out.println("📤 Sent search results to client.");
                }
                else if (input.startsWith("ADD_STORE_JSON:")) {

                    String jsonPath = input.split(":", 2)[1];
                    StringBuilder jsonContent = new StringBuilder();

                    try (BufferedReader fileReader = new BufferedReader(new FileReader(jsonPath))) {
                        String line;
                        while ((line = fileReader.readLine()) != null) {
                            jsonContent.append(line);
                        }
                    } catch (IOException e) {
                        writer.println("❌ Could not read JSON file: " + jsonPath);
                        continue;
                    }

                    try {
                        JSONArray storeArray = new JSONArray(jsonContent.toString());

                        for (int i = 0; i < storeArray.length(); i++) {
                            JSONObject storeJson = storeArray.getJSONObject(i);
                            String storeName = storeJson.getString("storeName");

                            int workerIndex = Math.abs(storeName.hashCode()) % workers.size();
                            WorkerInfo target = workers.get(workerIndex);

                            try (Socket ws = new Socket(target.getHost(), target.getPort());
                                 BufferedReader r = new BufferedReader(new InputStreamReader(ws.getInputStream()));
                                 PrintWriter w = new PrintWriter(ws.getOutputStream(), true)) {

                                r.readLine(); // skip welcome
                                w.println("ADD_STORE_DATA:" + storeJson.toString());

                                String response = r.readLine();
                                writer.println(response);
                            } catch (IOException e) {
                                writer.println("❌ Worker unreachable: " + target);
                            }
                        }

                    } catch (Exception e) {
                        writer.println("❌ Invalid JSON file.");
                    }
                } else if (input.startsWith("ADD_PRODUCT:")) {
                    String[] parts = input.split(":", 3);
                    if (parts.length != 3) {
                        writer.println("❌ Format: ADD_PRODUCT:storeName:productData");
                        continue;
                    }

                    String storeName = parts[1];
                    int workerIndex = Math.abs(storeName.hashCode()) % workers.size();
                    WorkerInfo target = workers.get(workerIndex);

                    try (Socket ws = new Socket(target.getHost(), target.getPort());
                         BufferedReader r = new BufferedReader(new InputStreamReader(ws.getInputStream()));
                         PrintWriter w = new PrintWriter(ws.getOutputStream(), true)) {

                        r.readLine();
                        w.println(input);
                        writer.println(r.readLine());
                    } catch (IOException e) {
                        writer.println("❌ Worker not responding.");
                    }
                } else if (input.startsWith("REMOVE_PRODUCT:") ||
                        input.startsWith("UPDATE_STOCK:") ||
                        input.startsWith("BUY:")) {

                    String[] parts = input.split(":", 3);
                    String storeName = parts[1];
                    int workerIndex = Math.abs(storeName.hashCode()) % workers.size();
                    WorkerInfo target = workers.get(workerIndex);

                    try (Socket ws = new Socket(target.getHost(), target.getPort());
                         BufferedReader r = new BufferedReader(new InputStreamReader(ws.getInputStream()));
                         PrintWriter w = new PrintWriter(ws.getOutputStream(), true)) {

                        r.readLine();
                        w.println(input);
                        writer.println(r.readLine());

                    } catch (IOException e) {
                        writer.println("❌ Worker not responding.");
                    }
                } else if (input.startsWith("SEARCH:") ||
                        input.startsWith("QUERY_SALES_BY_CATEGORY:") ||
                        input.startsWith("QUERY_SALES_BY_PRODUCT:")) {

                    List<String> allResponses = new ArrayList<>();

                    for (WorkerInfo worker : workers) {
                        try (Socket ws = new Socket(worker.getHost(), worker.getPort());
                             BufferedReader r = new BufferedReader(new InputStreamReader(ws.getInputStream()));
                             PrintWriter w = new PrintWriter(ws.getOutputStream(), true)) {

                            // Send the full search input first
                            w.println(input);

                            // Optional: skip welcome only if your Worker sends it *before* reading input
                            // String welcome = r.readLine();

                            String line;
                            while ((line = r.readLine()) != null) {
                                if (line.equals("END")) break;
                                allResponses.add(line);
                            }
                            System.out.println("✅ Sent search to worker: " + worker.getPort());
                        } catch (IOException e) {
                            allResponses.add("❌ Worker unreachable: " + worker);
                        }
                    }

                    // Reduce the merged responses
                    List<String> reduced = Reducer.reduceResponses(allResponses);
                    for (String res : reduced) {
                        writer.println(res);
                    }
                    writer.println("END");
                } else if (input.equalsIgnoreCase("exit")) {
                    break;
                } else {
                    writer.println("⚠️ Unknown command.");
                }
            }

        } catch (IOException e) {
            System.err.println("❌ Connection error: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}