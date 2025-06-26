import java.util.*;
import java.awt.Point;

// Graph structure logic class
public class Graph {
    // Adjacency list to store nodes and their connections
    private final Map<String, Set<String>> adjList;

    // Node positions for GUI (x, y)
    private final Map<String, Point> nodePositions;

    // Constructor initializes the adjacency list and position map
    public Graph() {
        adjList = new HashMap<>();
        nodePositions = new HashMap<>();
    }

    // Add a node to the graph if it doesn't already exist
    public void addNode(String node) {
        if (node == null || node.trim().isEmpty()) {
            System.out.println("Invalid node name.");
            return;
        }
        adjList.putIfAbsent(node.trim(), new HashSet<>());
    }

    // Remove a node and all edges connected to it
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

    // Add an undirected edge between two existing nodes
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

    // Remove an edge between two nodes if it exists
    public void removeEdge(String from, String to) {
        if (!adjList.containsKey(from) || !adjList.containsKey(to)) {
            System.out.println("One or both nodes do not exist.");
            return;
        }
        adjList.get(from).remove(to);
        adjList.get(to).remove(from);
    }

    // Return the neighbors of a given node
    public Set<String> getNeighbors(String node) {
        return adjList.getOrDefault(node, Collections.emptySet());
    }

    // Check if a node exists in the graph
    public boolean hasNode(String node) {
        return adjList.containsKey(node);
    }

    // Return all nodes in the graph
    public Set<String> getAllNodes() {
        return adjList.keySet();
    }

    // Check if two nodes are directly connected
    public boolean isConnected(String from, String to) {
        if (!adjList.containsKey(from)) return false;
        return adjList.get(from).contains(to);
    }

    // Return all edges in the graph as a list of string pairs
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

    // Set the (x, y) position for a node
    public void setNodePosition(String node, int x, int y) {
        if (hasNode(node)) {
            nodePositions.put(node, new Point(x, y));
        }
    }

    // Get the (x, y) position of a node
    public Point getNodePosition(String node) {
        return nodePositions.get(node);
    }

    // Get total number of nodes
    public int getNodeCount() {
        return adjList.size();
    }

    // Return a string representation of the graph structure
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String node : adjList.keySet()) {
            sb.append(node).append(" → ").append(adjList.get(node)).append("\n");
        }
        return sb.toString();
    }
}

// main method for testing only can be removed or ignored for GUI/BFS integration
/*
public static void main(String[] args) {
    Graph g = new Graph();

    // Sample nodes
    g.addNode("A");
    g.addNode("B");
    g.addNode("C");
    g.addNode(""); // Invalid node
    g.addNode(null); // Invalid node

    // Sample edges
    g.addEdge("A", "B");
    g.addEdge("A", "C");
    g.addEdge("A", "A"); // Invalid edge
    g.addEdge("A", "Z"); // Invalid edge (Z doesn't exist)

    // Display current graph
    System.out.println("Graph structure:");
    System.out.println(g);

    // Utility checks
    System.out.println("Contains node 'B'? " + g.hasNode("B"));
    System.out.println("All nodes: " + g.getAllNodes());
    System.out.println("A connected to B? " + g.isConnected("A", "B"));

    // Remove edge and node
    g.removeEdge("A", "B");
    g.removeNode("C");
    g.removeNode("Z"); // Invalid remove

    // Display updated graph
    System.out.println("\nUpdated graph:");
    System.out.println(g);
}
*/