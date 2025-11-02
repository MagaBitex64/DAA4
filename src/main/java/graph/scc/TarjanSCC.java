package graph.scc;

import graph.common.Graph;
import graph.common.Metrics;
import java.util.*;

public class TarjanSCC {
    private final Graph graph;
    private final Metrics metrics;
    private int[] ids;
    private int[] low;
    private boolean[] onStack;
    private Deque<Integer> stack;
    private int id;
    private List<List<Integer>> sccs;

    public TarjanSCC(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    public List<List<Integer>> findSCCs() {
        int n = graph.getNumVertices();
        ids = new int[n];
        low = new int[n];
        onStack = new boolean[n];
        stack = new ArrayDeque<>();
        sccs = new ArrayList<>();
        id = 0;

        Arrays.fill(ids, -1);

        metrics.startTimer();

        for (int i = 0; i < n; i++) {
            if (ids[i] == -1) {
                dfs(i);
            }
        }

        metrics.stopTimer();

        return sccs;
    }

    private void dfs(int at) {
        metrics.incrementDfsVisits();

        ids[at] = low[at] = id++;
        stack.push(at);
        metrics.incrementStackOperations();
        onStack[at] = true;

        for (Graph.Edge edge : graph.getNeighbors(at)) {
            metrics.incrementEdgeTraversals();
            int to = edge.to;

            if (ids[to] == -1) {
                dfs(to);
            }

            if (onStack[to]) {
                low[at] = Math.min(low[at], low[to]);
            }
        }

        if (ids[at] == low[at]) {
            List<Integer> scc = new ArrayList<>();

            while (true) {
                int node = stack.pop();
                metrics.incrementStackOperations();
                onStack[node] = false;
                scc.add(node);

                if (node == at) break;
            }

            sccs.add(scc);
        }
    }

    public Graph buildCondensation(List<List<Integer>> sccs) {
        int numSCCs = sccs.size();
        Graph condensation = new Graph(numSCCs);

        Map<Integer, Integer> vertexToSCC = new HashMap<>();
        for (int i = 0; i < sccs.size(); i++) {
            for (int vertex : sccs.get(i)) {
                vertexToSCC.put(vertex, i);
            }
            condensation.setTaskName(i, "SCC" + i + "_size" + sccs.get(i).size());
        }

        Set<String> addedEdges = new HashSet<>();
        for (int v = 0; v < graph.getNumVertices(); v++) {
            int fromSCC = vertexToSCC.get(v);

            for (Graph.Edge edge : graph.getNeighbors(v)) {
                int toSCC = vertexToSCC.get(edge.to);

                if (fromSCC != toSCC) {
                    String edgeKey = fromSCC + "->" + toSCC;
                    if (!addedEdges.contains(edgeKey)) {
                        condensation.addEdge(fromSCC, toSCC, edge.weight);
                        addedEdges.add(edgeKey);
                    }
                }
            }
        }

        return condensation;
    }

    public void printSCCs(List<List<Integer>> sccs) {
        System.out.println("\nStrongly Connected Components");
        System.out.println("Total SCCs: " + sccs.size());

        for (int i = 0; i < sccs.size(); i++) {
            List<Integer> scc = sccs.get(i);
            System.out.print("SCC " + i + " (size " + scc.size() + "): [");

            for (int j = 0; j < scc.size(); j++) {
                System.out.print(graph.getTaskName(scc.get(j)));
                if (j < scc.size() - 1) System.out.print(", ");
            }
            System.out.println("]");
        }
    }
}