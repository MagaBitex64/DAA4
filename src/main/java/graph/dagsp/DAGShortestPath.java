package graph.dagsp;

import graph.common.Graph;
import graph.common.Metrics;
import graph.topo.TopologicalSort;
import java.util.*;

public class DAGShortestPath {
    private final Graph graph;
    private final Metrics metrics;

    public DAGShortestPath(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    public PathResult shortestPaths(int source) {
        return computePaths(source, true);
    }

    public PathResult longestPaths(int source) {
        return computePaths(source, false);
    }

    private PathResult computePaths(int source, boolean shortest) {
        int n = graph.getNumVertices();

        TopologicalSort topoSort = new TopologicalSort(graph, new Metrics());
        List<Integer> topoOrder = topoSort.kahnSort();

        if (topoOrder == null) {
            return null;
        }

        double[] dist = new double[n];
        int[] parent = new int[n];
        Arrays.fill(dist, shortest ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY);
        Arrays.fill(parent, -1);
        dist[source] = 0;

        metrics.startTimer();

        for (int u : topoOrder) {
            if (dist[u] == (shortest ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY)) {
                continue;
            }

            for (Graph.Edge edge : graph.getNeighbors(u)) {
                metrics.incrementRelaxations();
                int v = edge.to;
                double newDist = dist[u] + edge.weight;

                boolean shouldUpdate = shortest ?
                        (newDist < dist[v]) : (newDist > dist[v]);

                if (shouldUpdate) {
                    dist[v] = newDist;
                    parent[v] = u;
                }
            }
        }

        metrics.stopTimer();

        return new PathResult(dist, parent, source, shortest);
    }

    public CriticalPath findCriticalPath() {
        int n = graph.getNumVertices();
        double maxLength = Double.NEGATIVE_INFINITY;
        int endVertex = -1;
        int startVertex = -1;

        for (int src = 0; src < n; src++) {
            PathResult result = longestPaths(src);
            if (result == null) continue;

            for (int dest = 0; dest < n; dest++) {
                if (result.distances[dest] != Double.NEGATIVE_INFINITY &&
                        result.distances[dest] > maxLength) {
                    maxLength = result.distances[dest];
                    endVertex = dest;
                    startVertex = src;
                }
            }
        }

        if (endVertex == -1) {
            return null;
        }

        PathResult finalResult = longestPaths(startVertex);
        List<Integer> path = reconstructPath(finalResult, endVertex);

        return new CriticalPath(path, maxLength);
    }

    private List<Integer> reconstructPath(PathResult result, int dest) {
        if (result == null || result.distances[dest] ==
                (result.shortest ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY)) {
            return new ArrayList<>();
        }

        List<Integer> path = new ArrayList<>();
        int current = dest;

        while (current != -1) {
            path.add(current);
            current = result.parent[current];
        }

        Collections.reverse(path);
        return path;
    }

    public void printShortestPaths(PathResult result) {
        System.out.println("\nShortest Paths from " + graph.getTaskName(result.source));

        for (int v = 0; v < graph.getNumVertices(); v++) {
            if (result.distances[v] == Double.POSITIVE_INFINITY) {
                System.out.println(graph.getTaskName(v) + ": unreachable");
            } else {
                List<Integer> path = reconstructPath(result, v);
                System.out.printf("%s: %.2f (path: ",
                        graph.getTaskName(v), result.distances[v]);
                printPath(path);
                System.out.println(")");
            }
        }
    }

    public void printCriticalPath(CriticalPath cp) {
        if (cp == null) {
            System.out.println("\nCritical Path");
            System.out.println("No critical path found (graph may have cycle)");
            return;
        }

        System.out.println("\nCritical Path (Longest)");
        System.out.printf("Length: %.2f\n", cp.length);
        System.out.print("Path: ");
        printPath(cp.path);
        System.out.println();
    }

    private void printPath(List<Integer> path) {
        for (int i = 0; i < path.size(); i++) {
            System.out.print(graph.getTaskName(path.get(i)));
            if (i < path.size() - 1) System.out.print(" -> ");
        }
    }

    public static class PathResult {
        public final double[] distances;
        public final int[] parent;
        public final int source;
        public final boolean shortest;

        public PathResult(double[] distances, int[] parent, int source, boolean shortest) {
            this.distances = distances;
            this.parent = parent;
            this.source = source;
            this.shortest = shortest;
        }
    }

    public static class CriticalPath {
        public final List<Integer> path;
        public final double length;

        public CriticalPath(List<Integer> path, double length) {
            this.path = path;
            this.length = length;
        }
    }
}