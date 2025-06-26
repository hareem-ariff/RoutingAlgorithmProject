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
        // if source and destination is missing gives log error
        if (!graph.hasNode(source) || !graph.hasNode(destination)) {
            logs.add("Invalid source or destination node.");
            return null;
        }
        // When both nodes are same
        if (source.equals(destination)) {
            logs.add("Source and destination are the same.");
            visitedOrder.add(source);
            return List.of(source);
        }

        logs.add("Starting BFS from: " + source);
        Queue<String> queue = new LinkedList<>();// for BFS traversal
        Set<String> visited = new HashSet<>();// to avoid revisiting nodes
        Map<String, String> parent = new HashMap<>();// to reconstruct the path later
        // Starts BFS
        queue.offer(source);
        visited.add(source);
        // when queue is not empty
        while (!queue.isEmpty()) {
            String current = queue.poll();// takes first node to visit
            logs.add("Dequeued: " + current);
            logs.add("");
            logs.add("Visiting: " + current);// logs records that currently visiting node
            visitedOrder.add(current);// saves the order

            if (current.equals(destination)) {
                logs.add("");
                logs.add("Destination reached: " + destination);
                logs.add("");
                // Reconstruct path
                List<String> path = new ArrayList<>();
                for (String at = destination; at != null; at = parent.get(at)) {
                    path.add(at);
                }
                Collections.reverse(path);
                logs.add("Shortest path: " + path);
                return path;
            }

            List<String> queuedNeighbors = new ArrayList<>();
            for (String neighbor : graph.getNeighbors(current)) {// checks each neighbour connected to the node
                if (!visited.contains(neighbor)) {// visit neighbour
                    queue.offer(neighbor);// add neighbour to the queue
                    visited.add(neighbor);// marks neighbour as visited
                    parent.put(neighbor, current);// records that neighbour was visited for building path later
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

    // Returns the traversal log
    public List<String> getLogs() {
        return logs;
    }

    // Returns the order in which nodes were visited (for animation or step-by-step visualization)
    public List<String> getVisitedOrder() {
        return visitedOrder;
    }
}
