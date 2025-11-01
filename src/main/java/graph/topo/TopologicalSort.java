package graph.topo;

import graph.common.Graph;
import graph.common.Metrics;
import java.util.*;

public class TopologicalSort {
    private final Graph graph;
    private final Metrics metrics;
    
    public TopologicalSort(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }
    
    public List<Integer> kahnSort() {
        int n = graph.getNumVertices();
        int[] inDegree = new int[n];
        
        for (int v = 0; v < n; v++) {
            for (Graph.Edge edge : graph.getNeighbors(v)) {
                inDegree[edge.to]++;
            }
        }
        
        Queue<Integer> queue = new LinkedList<>();
        for (int v = 0; v < n; v++) {
            if (inDegree[v] == 0) {
                queue.offer(v);
                metrics.incrementStackOperations();
            }
        }
        
        List<Integer> topoOrder = new ArrayList<>();
        
        metrics.startTimer();
        
        while (!queue.isEmpty()) {
            int v = queue.poll();
            metrics.incrementStackOperations();
            topoOrder.add(v);
            
            for (Graph.Edge edge : graph.getNeighbors(v)) {
                metrics.incrementEdgeTraversals();
                inDegree[edge.to]--;
                
                if (inDegree[edge.to] == 0) {
                    queue.offer(edge.to);
                    metrics.incrementStackOperations();
                }
            }
        }
        
        metrics.stopTimer();
        
        if (topoOrder.size() != n) {
            return null;
        }
        
        return topoOrder;
    }
    
    public List<Integer> dfsSort() {
        int n = graph.getNumVertices();
        boolean[] visited = new boolean[n];
        Deque<Integer> stack = new ArrayDeque<>();
        
        metrics.startTimer();
        
        for (int v = 0; v < n; v++) {
            if (!visited[v]) {
                dfsVisit(v, visited, stack);
            }
        }
        
        metrics.stopTimer();
        
        List<Integer> topoOrder = new ArrayList<>();
        while (!stack.isEmpty()) {
            topoOrder.add(stack.pop());
        }
        
        return topoOrder;
    }
    
    private void dfsVisit(int v, boolean[] visited, Deque<Integer> stack) {
        metrics.incrementDfsVisits();
        visited[v] = true;
        
        for (Graph.Edge edge : graph.getNeighbors(v)) {
            metrics.incrementEdgeTraversals();
            if (!visited[edge.to]) {
                dfsVisit(edge.to, visited, stack);
            }
        }
        
        stack.push(v);
        metrics.incrementStackOperations();
    }
    
    public void printTopoOrder(List<Integer> order) {
        if (order == null) {
            System.out.println("\n=== Topological Sort ===");
            System.out.println("ERROR: Graph contains a cycle!");
            return;
        }
        
        System.out.println("\n=== Topological Sort ===");
        System.out.print("Order: ");
        for (int i = 0; i < order.size(); i++) {
            System.out.print(graph.getTaskName(order.get(i)));
            if (i < order.size() - 1) System.out.print(" -> ");
        }
        System.out.println();
    }
}
