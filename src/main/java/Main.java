import graph.common.*;
import graph.scc.TarjanSCC;
import graph.topo.TopologicalSort;
import graph.dagsp.DAGShortestPath;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String[] datasets = {
                "small_dag_1", "small_cycle_1", "small_mixed_1",
                "medium_dag_1", "medium_scc_1", "medium_dense_1",
                "large_dag_1", "large_scc_1", "large_dense_1"
        };

        for (String dataset : datasets) {
            System.out.println("\nTESTING DATASET: " + dataset);
            processDataset("data/" + dataset + ".json");
        }

        System.out.println("\nALL TESTS COMPLETED");
    }

    public static void processDataset(String filepath) {
        try {
            Graph graph = DataLoader.loadGraph(filepath);
            System.out.println("\nLoaded graph: " + filepath);
            System.out.println("Vertices: " + graph.getNumVertices() + ", Edges: " + graph.countEdges());

            Metrics sccMetrics = new Metrics();
            TarjanSCC tarjan = new TarjanSCC(graph, sccMetrics);
            List<List<Integer>> sccs = tarjan.findSCCs();

            tarjan.printSCCs(sccs);
            System.out.println("\nSCC Metrics: " + sccMetrics);

            Graph condensation = tarjan.buildCondensation(sccs);
            System.out.println("\nCondensation Graph:");
            System.out.println("Vertices (SCCs): " + condensation.getNumVertices() + ", Edges: " + condensation.countEdges());

            Metrics topoMetrics = new Metrics();
            TopologicalSort topoSort = new TopologicalSort(condensation, topoMetrics);
            List<Integer> topoOrder = topoSort.kahnSort();

            topoSort.printTopoOrder(topoOrder);
            System.out.println("\nTopological Sort Metrics: " + topoMetrics);

            if (topoOrder != null) {
                System.out.println("\nDerived Task Order:");
                for (int sccIdx : topoOrder) {
                    List<Integer> scc = sccs.get(sccIdx);
                    System.out.print("  " + condensation.getTaskName(sccIdx) + ": [");
                    for (int i = 0; i < scc.size(); i++) {
                        System.out.print(graph.getTaskName(scc.get(i)));
                        if (i < scc.size() - 1) System.out.print(", ");
                    }
                    System.out.println("]");
                }
            }

            if (topoOrder != null) {
                Metrics spMetrics = new Metrics();
                DAGShortestPath dagSP = new DAGShortestPath(condensation, spMetrics);

                DAGShortestPath.PathResult shortestResult = dagSP.shortestPaths(0);
                dagSP.printShortestPaths(shortestResult);
                System.out.println("\nShortest Path Metrics: " + spMetrics);

                spMetrics.reset();
                DAGShortestPath.CriticalPath criticalPath = dagSP.findCriticalPath();
                dagSP.printCriticalPath(criticalPath);
                System.out.println("\nLongest Path Metrics: " + spMetrics);
            }

        } catch (IOException e) {
            System.err.println("Error loading dataset: " + filepath);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error processing dataset: " + filepath);
            e.printStackTrace();
        }
    }
}