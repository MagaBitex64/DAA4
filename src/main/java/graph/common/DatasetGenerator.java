package graph.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DatasetGenerator {

    public static void main(String[] args) {
        generateAllDatasets();
    }

    public static void generateAllDatasets() {
        generateDataset("small_dag_1", 6, 8, false, 0);
        generateDataset("small_cycle_1", 7, 10, true, 0);
        generateDataset("small_mixed_1", 8, 12, true, 0);

        generateDataset("medium_dag_1", 12, 18, false, 0);
        generateDataset("medium_scc_1", 15, 30, true, 0);
        generateDataset("medium_dense_1", 18, 60, true, 2);

        generateDataset("large_dag_1", 25, 50, false, 0);
        generateDataset("large_scc_1", 35, 100, true, 0);
        generateDataset("large_dense_1", 40, 180, true, 5);

        System.out.println("All datasets generated successfully!");
    }

    private static void generateDataset(String filename, int numNodes, int numEdges,
                                        boolean includeCycles, int source) {
        Random rand = new Random(filename.hashCode());

        Map<String, Object> dataset = new HashMap<>();
        dataset.put("directed", true);
        dataset.put("n", numNodes);
        dataset.put("source", source);
        dataset.put("weight_model", "edge");

        List<Map<String, Object>> edges = new ArrayList<>();
        Set<String> addedEdges = new HashSet<>();

        if (includeCycles) {
            int numCycles = Math.max(1, numNodes / 8);
            for (int c = 0; c < numCycles; c++) {
                int cycleSize = 2 + rand.nextInt(4);
                int start = rand.nextInt(numNodes);

                for (int i = 0; i < cycleSize; i++) {
                    int u = (start + i) % numNodes;
                    int v = (start + i + 1) % numNodes;

                    String edgeKey = u + "->" + v;
                    if (!addedEdges.contains(edgeKey) && u != v) {
                        edges.add(createEdge(u, v, rand));
                        addedEdges.add(edgeKey);
                    }
                }
            }
        }

        while (edges.size() < numEdges) {
            int u = rand.nextInt(numNodes);
            int v = rand.nextInt(numNodes);

            if (u == v) continue;

            if (!includeCycles && u > v) {
                int temp = u;
                u = v;
                v = temp;
            }

            String edgeKey = u + "->" + v;
            if (!addedEdges.contains(edgeKey)) {
                edges.add(createEdge(u, v, rand));
                addedEdges.add(edgeKey);
            }
        }

        dataset.put("edges", edges);

        saveDataset(dataset, "data/" + filename + ".json");
    }

    private static Map<String, Object> createEdge(int u, int v, Random rand) {
        Map<String, Object> edge = new HashMap<>();
        edge.put("u", u);
        edge.put("v", v);
        edge.put("w", 1 + rand.nextInt(5));
        return edge;
    }

    private static void saveDataset(Map<String, Object> dataset, String filepath) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter(filepath);
            gson.toJson(dataset, writer);
            writer.close();
            System.out.println("Generated: " + filepath);
        } catch (IOException e) {
            System.err.println("Error writing file: " + filepath);
            e.printStackTrace();
        }
    }
}