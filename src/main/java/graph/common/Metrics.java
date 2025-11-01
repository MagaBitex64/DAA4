package graph.common;

public class Metrics {
    private long startTime;
    private long endTime;
    private int dfsVisits;
    private int edgeTraversals;
    private int stackOperations;
    private int relaxations;
    
    public Metrics() {
        reset();
    }
    
    public void startTimer() {
        startTime = System.nanoTime();
    }
    
    public void stopTimer() {
        endTime = System.nanoTime();
    }
    
    public double getElapsedTimeMs() {
        return (endTime - startTime) / 1_000_000.0;
    }
    
    public void incrementDfsVisits() {
        dfsVisits++;
    }
    
    public void incrementEdgeTraversals() {
        edgeTraversals++;
    }
    
    public void incrementStackOperations() {
        stackOperations++;
    }
    
    public void incrementRelaxations() {
        relaxations++;
    }
    
    public int getDfsVisits() { return dfsVisits; }
    public int getEdgeTraversals() { return edgeTraversals; }
    public int getStackOperations() { return stackOperations; }
    public int getRelaxations() { return relaxations; }
    
    public void reset() {
        startTime = 0;
        endTime = 0;
        dfsVisits = 0;
        edgeTraversals = 0;
        stackOperations = 0;
        relaxations = 0;
    }
    
    @Override
    public String toString() {
        return String.format("Time: %.3f ms | DFS Visits: %d | Edges: %d | Stack Ops: %d | Relaxations: %d",
                getElapsedTimeMs(), dfsVisits, edgeTraversals, stackOperations, relaxations);
    }
}
