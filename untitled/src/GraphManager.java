import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

// GUI class for managing the graph
public class GraphManager extends JFrame {
    // GUI components
    private final JTextField nodeField;   // Input field for adding/removing a node
    private final JTextField fromField;   // Input for 'from' node of an edge
    private final JTextField toField;     // Input for 'to' node of an edge
    private final JTextArea displayArea;  // Text area to display the graph
    private final Graph graph;            // Instance of the graph logic class

    public GraphManager() {
        // Basic window settings
        setTitle("Graph Manager");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        graph = new Graph();  // Initialize the graph object

        // Main layout panel with vertical stacking
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        //  NODE SECTION
        JPanel nodePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        nodePanel.setBackground(Color.WHITE);
        nodeField = new JTextField(10);
        JButton addNodeBtn = createButton("Add Node");
        JButton removeNodeBtn = createButton("Remove Node");
        nodePanel.add(new JLabel("Node Name:"));
        nodePanel.add(nodeField);
        nodePanel.add(addNodeBtn);
        nodePanel.add(removeNodeBtn);
        mainPanel.add(nodePanel);

        //  EDGE SECTION
        JPanel edgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        edgePanel.setBackground(Color.WHITE);
        fromField = new JTextField(5);
        toField = new JTextField(5);
        JButton addEdgeBtn = createButton("Add Edge");
        JButton removeEdgeBtn = createButton("Remove Edge");
        edgePanel.add(new JLabel("From Node:"));
        edgePanel.add(fromField);
        edgePanel.add(new JLabel("To Node:"));
        edgePanel.add(toField);
        edgePanel.add(addEdgeBtn);
        edgePanel.add(removeEdgeBtn);
        mainPanel.add(edgePanel);

        //  DISPLAY AREA
        displayArea = new JTextArea(12, 50);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        displayArea.setBackground(new Color(245, 245, 245));
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Graph Output"));
        mainPanel.add(scrollPane);

        // Add main panel to frame
        add(mainPanel);

        //  BUTTON ACTIONS

        // Add node button
        addNodeBtn.addActionListener(e -> {
            String node = nodeField.getText().trim();
            if (!node.isEmpty()) {
                graph.addNode(node);
                updateDisplay();
                nodeField.setText("");
            }
        });

        // Remove node button
        removeNodeBtn.addActionListener(e -> {
            String node = nodeField.getText().trim();
            if (!node.isEmpty()) {
                graph.removeNode(node);
                updateDisplay();
                nodeField.setText("");
            }
        });

        // Add edge button
        addEdgeBtn.addActionListener(e -> {
            String from = fromField.getText().trim();
            String to = toField.getText().trim();
            if (!from.isEmpty() && !to.isEmpty()) {
                graph.addEdge(from, to);
                updateDisplay();
                fromField.setText("");
                toField.setText("");
            }
        });

        // Remove edge button
        removeEdgeBtn.addActionListener(e -> {
            String from = fromField.getText().trim();
            String to = toField.getText().trim();
            if (!from.isEmpty() && !to.isEmpty()) {
                graph.removeEdge(from, to);
                updateDisplay();
                fromField.setText("");
                toField.setText("");
            }
        });

        setVisible(true);  // Show the window
    }

    // Refresh the text area with current graph structure
    private void updateDisplay() {
        displayArea.setText(graph.toString());
    }

    // Create styled JButton for consistent look
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(72, 133, 237)); // Blue color
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GraphManager::new); // Run GUI on event thread
    }
}

// Graph structure logic class
class Graph {
    private final Map<String, Set<String>> adjList;  // Node and its connections

    public Graph() {
        adjList = new HashMap<>();
    }

    // Add a node if not already present
    public void addNode(String node) {
        adjList.putIfAbsent(node, new HashSet<>());
    }

    // Remove a node and disconnect it from all others
    public void removeNode(String node) {
        if (!adjList.containsKey(node)) return;
        adjList.remove(node);
        for (Set<String> neighbors : adjList.values()) {
            neighbors.remove(node);
        }
    }

    // Add an undirected edge between two nodes
    public void addEdge(String from, String to) {
        if (!adjList.containsKey(from) || !adjList.containsKey(to)) return;
        adjList.get(from).add(to);
        adjList.get(to).add(from);
    }

    // Remove the edge between two nodes
    public void removeEdge(String from, String to) {
        if (adjList.containsKey(from)) adjList.get(from).remove(to);
        if (adjList.containsKey(to)) adjList.get(to).remove(from);
    }

    // Return neighbors of a given node
    public Set<String> getNode(String node) {
        return adjList.get(node);
    }

    // String representation of the entire graph
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String node : adjList.keySet()) {
            sb.append(node).append(" → ").append(adjList.get(node)).append("\n");
        }
        return sb.toString();
    }
}
