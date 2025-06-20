import java.util.*;

// Graph structure logic class
 public class Graph {
    // Adjacency list to store nodes and their connections
    private final Map<String, Set<String>> adjList;

    // Constructor initializes the adjacency list
    public Graph() {
        adjList = new HashMap<>();
    }

    // Add a node to the graph if it doesn't already exist
    public void addNode(String node) {
        adjList.putIfAbsent(node, new HashSet<>());
    }

    // Remove a node and all edges connected to it
    public void removeNode(String node) {
        if (!adjList.containsKey(node)) return;
        adjList.remove(node);
        for (Set<String> neighbors : adjList.values()) {
            neighbors.remove(node);
        }
    }

    // Add an undirected edge between two existing nodes
    public void addEdge(String from, String to) {
        if (!adjList.containsKey(from) || !adjList.containsKey(to)) return;
        adjList.get(from).add(to);
        adjList.get(to).add(from);
    }

    // Remove an edge between two nodes if it exists
    public void removeEdge(String from, String to) {
        if (adjList.containsKey(from)) adjList.get(from).remove(to);
        if (adjList.containsKey(to)) adjList.get(to).remove(from);
    }

    // Return the neighbors of a given node
    public Set<String> getNode(String node) {
        return adjList.get(node);
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

    // Main method for simple testing without GUI
    public static void main(String[] args) {
        Graph g = new Graph();

        // Sample nodes
        g.addNode("A");
        g.addNode("B");
        g.addNode("C");

        // Sample edges
        g.addEdge("A", "B");
        g.addEdge("A", "C");

        // Display current graph
        System.out.println("Graph structure:");
        System.out.println(g);

        // Remove edge and node
        g.removeEdge("A", "B");
        g.removeNode("C");

        // Display updated graph
        System.out.println("\nUpdated graph:");
        System.out.println(g);
    }
}
