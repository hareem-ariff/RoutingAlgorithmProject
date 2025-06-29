import java.util.*;

// Feature 4: Implements the BFS routing algorithm
// Feature 8: Logs routing decisions step by step
// Feature 9: Handles edge cases like invalid nodes, same source/destination, or no path
public class BFSRouter {
    private final Graph graph;                   // Graph reference
    private final List<String> logs;             // Feature 8: Logs of each step
    private final List<String> visitedOrder;     // Used for GUI animation (Feature 6)

    public BFSRouter(Graph graph) {
        this.graph = graph;
        this.logs = new ArrayList<>();
        this.visitedOrder = new ArrayList<>();
    }

    // Feature 4: BFS traversal to find shortest path
    // Note: BFS works on unweighted graphs. It treats every edge as equal-cost.
    public List<String> runBFS(String source, String destination) {
        logs.clear();         // Clear previous run logs
        visitedOrder.clear(); // Clear visited history

        // Feature 9: Edge case - invalid nodes
        if (!graph.hasNode(source) || !graph.hasNode(destination)) {
            logs.add("Invalid source or destination node.");
            return null;
        }

        // Feature 9: Edge case - same source and destination
        if (source.equals(destination)) {
            logs.add("Source and destination are the same.");
            visitedOrder.add(source);
            return List.of(source);
        }

        logs.add("Starting BFS from: " + source);
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>();

        queue.offer(source);
        visited.add(source);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            logs.add("Dequeued: " + current);
            logs.add("");
            logs.add("Visiting: " + current);
            visitedOrder.add(current);

            if (current.equals(destination)) {
                logs.add("");
                logs.add("Destination reached: " + destination);
                logs.add("");
                List<String> path = new ArrayList<>();
                for (String at = destination; at != null; at = parent.get(at)) {
                    path.add(at);
                }
                Collections.reverse(path);
                logs.add("Shortest path: " + path);
                return path;
            }

            List<String> queuedNeighbors = new ArrayList<>();
            for (String neighbor : graph.getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    queue.offer(neighbor);
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    queuedNeighbors.add(neighbor);
                }
            }
            if (!queuedNeighbors.isEmpty()) {
                logs.add("Queueing: " + String.join(", ", queuedNeighbors));
                logs.add("");
            }
        }
        logs.add("No path found from " + source + " to " + destination);
        return null;
    }

    public List<String> getLogs() {
        return logs;
    }

    public List<String> getVisitedOrder() {
        return visitedOrder;
    }
}
