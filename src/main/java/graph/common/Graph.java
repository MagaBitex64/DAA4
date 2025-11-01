package graph.common;

import java.util.*;

public class Graph {
    private final int numVertices;
    private final List<List<Edge>> adjacencyList;
    private final Map<String, Integer> taskNameToId;
    private final Map<Integer, String> idToTaskName;
    
    public Graph(int numVertices) {
        this.numVertices = numVertices;
        this.adjacencyList = new ArrayList<>(numVertices);
        this.taskNameToId = new HashMap<>();
        this.idToTaskName = new HashMap<>();
        
        for (int i = 0; i < numVertices; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }
    
    public void addEdge(int from, int to, double weight) {
        adjacencyList.get(from).add(new Edge(to, weight));
    }
    
    public void addEdge(int from, int to) {
        addEdge(from, to, 1.0);
    }
    
    public void setTaskName(int id, String name) {
        taskNameToId.put(name, id);
        idToTaskName.put(id, name);
    }
    
    public String getTaskName(int id) {
        return idToTaskName.getOrDefault(id, "Task" + id);
    }
    
    public Integer getTaskId(String name) {
        return taskNameToId.get(name);
    }
    
    public int getNumVertices() {
        return numVertices;
    }
    
    public List<Edge> getNeighbors(int vertex) {
        return adjacencyList.get(vertex);
    }
    
    public List<List<Edge>> getAdjacencyList() {
        return adjacencyList;
    }
    
    public Graph transpose() {
        Graph transposed = new Graph(numVertices);
        
        for (Map.Entry<Integer, String> entry : idToTaskName.entrySet()) {
            transposed.setTaskName(entry.getKey(), entry.getValue());
        }
        
        for (int v = 0; v < numVertices; v++) {
            for (Edge edge : adjacencyList.get(v)) {
                transposed.addEdge(edge.to, v, edge.weight);
            }
        }
        
        return transposed;
    }
    
    public int countEdges() {
        int count = 0;
        for (List<Edge> edges : adjacencyList) {
            count += edges.size();
        }
        return count;
    }
    
    public static class Edge {
        public final int to;
        public final double weight;
        
        public Edge(int to, double weight) {
            this.to = to;
            this.weight = weight;
        }
        
        public Edge(int to) {
            this(to, 1.0);
        }
    }
}
