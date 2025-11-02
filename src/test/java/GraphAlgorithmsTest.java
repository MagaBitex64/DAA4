import graph.common.*;
import graph.scc.TarjanSCC;
import graph.topo.TopologicalSort;
import graph.dagsp.DAGShortestPath;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class GraphAlgorithmsTest {

    @Test
    public void testSimpleDAG() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 1.0);
        graph.addEdge(0, 2, 3.0);

        Metrics metrics = new Metrics();
        TopologicalSort topoSort = new TopologicalSort(graph, metrics);
        List<Integer> order = topoSort.kahnSort();

        assertNotNull(order);
        assertEquals(3, order.size());
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(1) < order.indexOf(2));
    }

    @Test
    public void testSingleSCC() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 1.0);
        graph.addEdge(2, 0, 1.0);

        Metrics metrics = new Metrics();
        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals(1, sccs.size());
        assertEquals(3, sccs.get(0).size());
    }

    @Test
    public void testMultipleSCCs() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 0, 1.0);
        graph.addEdge(2, 3, 1.0);
        graph.addEdge(3, 2, 1.0);
        graph.addEdge(1, 2, 1.0);

        Metrics metrics = new Metrics();
        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals(2, sccs.size());
    }

    @Test
    public void testShortestPath() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(0, 2, 4.0);
        graph.addEdge(1, 2, 2.0);
        graph.addEdge(2, 3, 1.0);
        graph.addEdge(1, 3, 5.0);

        Metrics metrics = new Metrics();
        DAGShortestPath dagSP = new DAGShortestPath(graph, metrics);
        DAGShortestPath.PathResult result = dagSP.shortestPaths(0);

        assertNotNull(result);
        assertEquals(0.0, result.distances[0], 0.001);
        assertEquals(1.0, result.distances[1], 0.001);
        assertEquals(3.0, result.distances[2], 0.001);
        assertEquals(4.0, result.distances[3], 0.001);
    }

    @Test
    public void testLongestPath() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 3.0);
        graph.addEdge(0, 2, 2.0);
        graph.addEdge(1, 3, 4.0);
        graph.addEdge(2, 3, 1.0);

        Metrics metrics = new Metrics();
        DAGShortestPath dagSP = new DAGShortestPath(graph, metrics);
        DAGShortestPath.CriticalPath cp = dagSP.findCriticalPath();

        assertNotNull(cp);
        assertEquals(7.0, cp.length, 0.001);
    }

    @Test
    public void testCycleDetection() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 1.0);
        graph.addEdge(2, 0, 1.0);

        Metrics metrics = new Metrics();
        TopologicalSort topoSort = new TopologicalSort(graph, metrics);
        List<Integer> order = topoSort.kahnSort();

        assertNull(order);
    }

    @Test
    public void testEmptyGraph() {
        Graph graph = new Graph(0);

        Metrics metrics = new Metrics();
        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertTrue(sccs.isEmpty());
    }

    @Test
    public void testSingleVertex() {
        Graph graph = new Graph(1);

        Metrics metrics = new Metrics();
        TopologicalSort topoSort = new TopologicalSort(graph, metrics);
        List<Integer> order = topoSort.kahnSort();

        assertNotNull(order);
        assertEquals(1, order.size());
        assertEquals(0, order.get(0));
    }

    @Test
    public void testDisconnectedGraph() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(2, 3, 1.0);

        Metrics metrics = new Metrics();
        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals(4, sccs.size());
    }
}