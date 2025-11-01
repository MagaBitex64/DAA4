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

        List<Map<String, Object>> nodes =
                (List<Map<String, Object>>) data.get("nodes");
        int numNodes = nodes.size();

        Graph graph = new Graph(numNodes);

        for (Map<String, Object> node : nodes) {
            int id = ((Double) node.get("id")).intValue();
            String name = (String) node.get("name");
            graph.setTaskName(id, name);
        }

        List<Map<String, Object>> edges =
                (List<Map<String, Object>>) data.get("edges");

        for (Map<String, Object> edge : edges) {
            int from = ((Double) edge.get("from")).intValue();
            int to = ((Double) edge.get("to")).intValue();
            double weight = (Double) edge.get("weight");

            graph.addEdge(from, to, weight);
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
}