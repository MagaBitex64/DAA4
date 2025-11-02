package graph.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DataLoader {

    public static Graph loadGraph(String filepath) throws IOException {
        Gson gson = new Gson();
        FileReader reader = new FileReader(filepath);

        Map<String, Object> data = gson.fromJson(reader,
                new TypeToken<Map<String, Object>>(){}.getType());
        reader.close();

        int numNodes = ((Double) data.get("n")).intValue();
        Graph graph = new Graph(numNodes);

        for (int i = 0; i < numNodes; i++) {
            graph.setTaskName(i, "Task" + i);
        }

        List<Map<String, Object>> edges =
                (List<Map<String, Object>>) data.get("edges");

        for (Map<String, Object> edge : edges) {
            int u = ((Double) edge.get("u")).intValue();
            int v = ((Double) edge.get("v")).intValue();
            double w = ((Double) edge.get("w")).doubleValue();

            graph.addEdge(u, v, w);
        }

        return graph;
    }

    public static Map<String, Object> getMetadata(String filepath) throws IOException {
        Gson gson = new Gson();
        FileReader reader = new FileReader(filepath);

        Map<String, Object> data = gson.fromJson(reader,
                new TypeToken<Map<String, Object>>(){}.getType());
        reader.close();

        return data;
    }

    public static int getSource(String filepath) throws IOException {
        Map<String, Object> data = getMetadata(filepath);
        if (data.containsKey("source")) {
            return ((Double) data.get("source")).intValue();
        }
        return 0;
    }
}