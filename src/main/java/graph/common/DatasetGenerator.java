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
        generateDataset("small_dag_1", 6, 8, false, 0.3, "Simple DAG");
        generateDataset("small_cycle_1", 7, 10, true, 0.35, "Simple with 1 cycle");
        generateDataset("small_mixed_1", 8, 12, true, 0.4, "Mixed with 2 cycles");

        generateDataset("medium_dag_1", 12, 18, false, 0.25, "Medium DAG sparse");
        generateDataset("medium_scc_1", 15, 30, true, 0.35, "Medium with multiple SCCs");
        generateDataset("medium_dense_1", 18, 60, true, 0.45, "Medium dense with cycles");

        generateDataset("large_dag_1", 25, 50, false, 0.2, "Large sparse DAG");
        generateDataset("large_scc_1", 35, 100, true, 0.25, "Large with multiple SCCs");
        generateDataset("large_dense_1", 40, 180, true, 0.35, "Large dense graph");

        System.out.println("All datasets generated successfully!");
    }

    private static void generateDataset(String filename, int numNodes, int numEdges,
                                        boolean includeCycles, double density,
                                        String description) {
        Random rand = new Random(filename.hashCode());

        Map<String, Object> dataset = new HashMap<>();
        dataset.put("description", description);
        dataset.put("num_nodes", numNodes);
        dataset.put("num_edges", numEdges);
        dataset.put("has_cycles", includeCycles);

        List<Map<String, Object>> nodes = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", i);
            node.put("name", "Task" + i);
            node.put("duration", 1.0 + rand.nextDouble() * 10.0);
            nodes.add(node);
        }
        dataset.put("nodes", nodes);

        List<Map<String, Object>> edges = new ArrayList<>();
        Set<String> addedEdges = new HashSet<>();

        if (includeCycles) {
            int numCycles = Math.max(1, numNodes / 8);
            for (int c = 0; c < numCycles; c++) {
                int cycleSize = 2 + rand.nextInt(4);
                int start = rand.nextInt(numNodes);

                for (int i = 0; i < cycleSize; i++) {
                    int from = (start + i) % numNodes;
                    int to = (start + i + 1) % numNodes;

                    String edgeKey = from + "->" + to;
                    if (!addedEdges.contains(edgeKey) && from != to) {
                        edges.add(createEdge(from, to, rand));
                        addedEdges.add(edgeKey);
                    }
                }
            }
        }

        while (edges.size() < numEdges) {
            int from = rand.nextInt(numNodes);
            int to = rand.nextInt(numNodes);

            if (from == to) continue;

            if (!includeCycles && from > to) {
                int temp = from;
                from = to;
                to = temp;
            }

            String edgeKey = from + "->" + to;
            if (!addedEdges.contains(edgeKey)) {
                edges.add(createEdge(from, to, rand));
                addedEdges.add(edgeKey);
            }
        }

        dataset.put("edges", edges);

        saveDataset(dataset, "data/" + filename + ".json");
    }

    private static Map<String, Object> createEdge(int from, int to, Random rand) {
        Map<String, Object> edge = new HashMap<>();
        edge.put("from", from);
        edge.put("to", to);
        edge.put("weight", 1.0 + rand.nextDouble() * 5.0);
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