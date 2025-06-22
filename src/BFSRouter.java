import java.util.*;

public class BFSRouter {
    private final Graph graph;                 // Saves the graph on which this class will work
    private final List<String> logs;           // list for storing logs

    // Constructor for initializing graph and preparing log list
    public BFSRouter(Graph graph) {
        this.graph = graph;
        this.logs = new ArrayList<>();
    }
    // To find shortest path from source to destination node
    public List<String> findShortestPath(String source, String destination) {
        logs.clear();  // Clear any previous log entries

        // Check if source and destination exist in the graph
        if (!graph.hasNode(source) || !graph.hasNode(destination)) {
            logs.add("Invalid source or destination node.");
            return null;
        }

        // If source and destination are the same, return immediately
        if (source.equals(destination)) {
            logs.add("Source and destination are the same.");
            return List.of(source);
        }

        // Prepare for BFS traversal
        logs.add("Starting BFS from: " + source);// Start BFS from source node
        Queue<String> queue = new LinkedList<>();// Creates a queue to follow BFS rule
        Set<String> visited = new HashSet<>();// Keeps track of all the visited nodes
        Map<String, String> parent = new HashMap<>();  // Tracks the path back from destination

        queue.offer(source);      // Start from the source node
        visited.add(source);      // Mark the source as visited

        // Loops runs as long as there are nodes to explore
        while (!queue.isEmpty()) {
            String current = queue.poll();// Takes first node and calls it current
            logs.add("Visiting: " + current);// logs that now we are visiting this node

            // Gets all neighbours of the current node
            for (String neighbor : graph.getNode(current)) {
                if (!visited.contains(neighbor)) {// if neighbour is not visited, explore it
                    queue.offer(neighbor);             // Add neighbor to the queue
                    visited.add(neighbor);             // Mark neighbor as visited
                    parent.put(neighbor, current);     // Track parent for path reconstruction
                    logs.add("Queueing: " + neighbor);

                    // If destination is found, stop early
                    if (neighbor.equals(destination)) {
                        logs.add("Destination reached: " + destination);
                        break;
                    }
                }
            }
        }

        // If destination was not in parent map give no path exists
        if (!parent.containsKey(destination)) {
            logs.add("No path found from " + source + " to " + destination);
            return null;
        }

        List<String> path = new ArrayList<>();// Creates an empty list to store final shortest path
        for (String at = destination; at != null; at = parent.get(at)) {// Builds the path in reverse
            path.add(at);
        }
        Collections.reverse(path);  // to built path backwards
        logs.add("Shortest path: " + path);// log the final path

        return path;//returns the final list of path
    }
    // To return list of logs collected
    public List<String> getLogs() {
        return logs;
    }
}
