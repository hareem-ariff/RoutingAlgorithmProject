import java.util.*;

public class BFSRouter {
    private final Graph graph;                   // Graph reference
    private final List<String> logs;             // Logs of each step
    private final List<String> visitedOrder;     // Nodes visited in order (for GUI use)

    // Constructor
    public BFSRouter(Graph graph) {
        this.graph = graph;
        this.logs = new ArrayList<>();
        this.visitedOrder = new ArrayList<>();
    }

    // BFS traversal to find shortest path between source and destination
    public List<String> runBFS(String source, String destination) {
        logs.clear();         // Clear previous run logs
        visitedOrder.clear(); // Clear visited history

        // Check if either the source or destination node doesn't exist in the graph
        if (!graph.hasNode(source) || !graph.hasNode(destination)) {
            logs.add("Invalid source or destination node.");
            return null;
        }

        // If source and destination are the same, return that single node as the path
        if (source.equals(destination)) {
            logs.add("Source and destination are the same.");
            visitedOrder.add(source);
            return List.of(source);
        }

        logs.add("Starting BFS from: " + source);
        Queue<String> queue = new LinkedList<>();    // for BFS traversal
        Set<String> visited = new HashSet<>();       // to avoid revisiting nodes
        Map<String, String> parent = new HashMap<>();// to reconstruct the path later

        // Starts BFS
        queue.offer(source);
        visited.add(source);

        // Continue traversal while there are nodes to explore
        while (!queue.isEmpty()) {
            String current = queue.poll(); // Remove and retrieve the next node to visit from the queue
            logs.add("Visiting: " + current); // Log that we are currently visiting this node
            visitedOrder.add(current);         // Add current node to the list showing the order of visits

            // Loop through all neighbors (connected nodes) of the current node
            for (String neighbor : graph.getNeighbors(current)) {
                // Only proceed if the neighbor hasn't been visited yet
                if (!visited.contains(neighbor)) {
                    queue.offer(neighbor);                // Add the neighbor to the queue for future visiting
                    visited.add(neighbor);                // Mark the neighbor as visited to avoid revisiting
                    parent.put(neighbor, current);        // Save the parent of the neighbor to reconstruct the path later
                    logs.add("Queueing: " + neighbor);   // Log that we are queueing this neighbor for future visit

                    if (neighbor.equals(destination)) {
                        logs.add("Destination reached: " + destination);
                        break;
                    }
                }
            }
        }

        if (!parent.containsKey(destination)) {
            logs.add("No path found from " + source + " to " + destination);
            return null;
        }

        List<String> path = new ArrayList<>(); // Prepare a list to build the shortest path
        // Walk backward from destination to source using the parent map to build the path
        for (String at = destination; at != null; at = parent.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        logs.add("Shortest path: " + path);

        return path;
    }

    // Returns the traversal log
    public List<String> getLogs() {
        return logs;
    }

    // Returns the order in which nodes were visited (for animation or step-by-step visualization)
    public List<String> getVisitedOrder() {
        return visitedOrder;
    }
}
