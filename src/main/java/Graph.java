import java.util.*;
import java.awt.Point;

// Feature 2: Core graph structure logic for storing nodes and edges
// Note: BFS assumes all edges have equal weight (unweighted graph).
// No need to store weights since BFS finds the shortest path based on hop count, not edge cost.
// Feature 7: Allows dynamic changes in topology (add/remove nodes and edges)
public class Graph {
    // Adjacency list to store nodes and their connections
    private final Map<String, Set<String>> adjList;

    // Node positions for GUI (x, y) - supports Feature 6: Visualization
    private final Map<String, Point> nodePositions;

    public Graph() {
        adjList = new HashMap<>();
        nodePositions = new HashMap<>();
    }

    // Feature 2 + 7: Add a node to the graph
    public void addNode(String node) {
        if (node == null || node.trim().isEmpty()) {
            System.out.println("Invalid node name.");
            return;
        }
        adjList.putIfAbsent(node.trim(), new HashSet<>());
    }

    // Feature 7: Remove a node and its edges
    public void removeNode(String node) {
        if (!adjList.containsKey(node)) {
            System.out.println("Node does not exist.");
            return;
        }
        adjList.remove(node);
        nodePositions.remove(node);
        for (Set<String> neighbors : adjList.values()) {
            neighbors.remove(node);
        }
    }

    // Feature 2 + 7: Add an undirected edge between two nodes
    public void addEdge(String from, String to) {
        if (from == null || to == null || from.equals(to)) {
            System.out.println("Invalid edge.");
            return;
        }
        if (!adjList.containsKey(from) || !adjList.containsKey(to)) {
            System.out.println("One or both nodes do not exist.");
            return;
        }
        adjList.get(from).add(to);
        adjList.get(to).add(from);
    }

    // Feature 7: Remove an edge between two nodes
    public void removeEdge(String from, String to) {
        if (!adjList.containsKey(from) || !adjList.containsKey(to)) {
            System.out.println("One or both nodes do not exist.");
            return;
        }
        adjList.get(from).remove(to);
        adjList.get(to).remove(from);
    }

    // Helper: Return neighbors of a node
    public Set<String> getNeighbors(String node) {
        return adjList.getOrDefault(node, Collections.emptySet());
    }

    // Helper: Check node existence
    public boolean hasNode(String node) {
        return adjList.containsKey(node);
    }

    // Helper: Return all nodes
    public Set<String> getAllNodes() {
        return adjList.keySet();
    }

    // Helper: Check if nodes are directly connected
    public boolean isConnected(String from, String to) {
        if (!adjList.containsKey(from)) return false;
        return adjList.get(from).contains(to);
    }

    // Helper: Return all edges
    public List<String[]> getAllEdges() {
        List<String[]> edges = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        for (String from : adjList.keySet()) {
            for (String to : adjList.get(from)) {
                String key = from + "-" + to;
                String reverseKey = to + "-" + from;
                if (!visited.contains(reverseKey)) {
                    edges.add(new String[]{from, to});
                    visited.add(key);
                }
            }
        }
        return edges;
    }

    // Feature 6: Store node positions for GUI visualization
    public void setNodePosition(String node, int x, int y) {
        if (hasNode(node)) {
            nodePositions.put(node, new Point(x, y));
        }
    }

    // Feature 6: Get node position
    public Point getNodePosition(String node) {
        return nodePositions.get(node);
    }

    // Helper: Total number of nodes
    public int getNodeCount() {
        return adjList.size();
    }

    // Helper: Graph structure as string (useful for testing/debugging)
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String node : adjList.keySet()) {
            sb.append(node).append(" → ").append(adjList.get(node)).append("\n");
        }
        return sb.toString();
    }
}
