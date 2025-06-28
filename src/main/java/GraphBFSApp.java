// JavaFX imports and standard Java utilities
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.effect.DropShadow;
import javafx.util.Duration;

// Main application class for the Graph BFS Visualizer
public class GraphBFSApp extends Application {
    // Core graph logic and router instance
    private final Graph graph = new Graph();
    private BFSRouter bfsRouter = new BFSRouter(graph);

    // UI components
    private final Pane graphPane = new Pane();                // Pane to draw the graph
    private final TextArea logArea = new TextArea();          // TextArea to display BFS logs
    private final ComboBox<String> sourceBox = new ComboBox<>();
    private final ComboBox<String> destBox = new ComboBox<>();
    private final TextField nodeField = new TextField();      // Input field for node name
    private final TextField edgeFromField = new TextField();  // Input field for edge start
    private final TextField edgeToField = new TextField();    // Input field for edge end

    // Constants
    private static final int NODE_RADIUS = 22;
    private static final int ANIMATION_DELAY_MS = 400;

    // UI tracking maps
    private final Map<String, Circle> nodeCircles = new HashMap<>();
    private final Map<String, Label> nodeLabels = new HashMap<>();

    // Entry point for JavaFX application
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Graph BFS Visualizer");

        // Input field placeholders
        nodeField.setPromptText("Node name");
        edgeFromField.setPromptText("From node");
        edgeToField.setPromptText("To node");
        edgeFromField.setPrefWidth(120);
        edgeToField.setPrefWidth(120);

        // Layout containers
        HBox mainBox = new HBox();
        VBox leftPane = new VBox();
        VBox controlsBox = new VBox();

        mainBox.setId("mainBox");
        leftPane.setId("leftPane");
        controlsBox.setId("controlsBox");

        // Labels and input layout for nodes
        Label nodeLabel = new Label("Node");
        HBox nodeInputBox = new HBox(10, nodeField, btn("Add", e -> addNode()), btn("Remove", e -> removeNode()));
        VBox nodeBox = new VBox(4, nodeLabel, nodeInputBox);

        // Labels and input layout for edges
        Label edgeLabel = new Label("Edge");
        HBox edgeInputBox = new HBox(10, edgeFromField, edgeToField, btn("Add", e -> addEdge()), btn("Remove", e -> removeEdge()));
        VBox edgeBox = new VBox(4, edgeLabel, edgeInputBox);

        // Source and destination node selectors
        VBox sourceBoxContainer = new VBox(4, new Label("Source"), sourceBox);
        VBox destBoxContainer = new VBox(4, new Label("Destination"), destBox);
        HBox srcDestBox = new HBox(22, sourceBoxContainer, destBoxContainer);

        // Run BFS button
        Button bfsBtn = btn("Run BFS", e -> runBFS());
        bfsBtn.setId("runBFSBtn");
        bfsBtn.setPrefSize(240, 54);
        bfsBtn.setStyle(bfsBtn.getStyle() + ";-fx-font-size: 20px; -fx-font-weight: 800;");

        // Add control sections to controlsBox
        controlsBox.getChildren().addAll(nodeBox, edgeBox, srcDestBox, bfsBtn);

        // Log area for BFS output
        VBox logBox = new VBox();
        logBox.setId("logBox");
        Label logLabel = new Label("BFS Log:");
        logLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #ffe7b2;");
        logArea.setEditable(false);
        logArea.setPrefHeight(220);
        logArea.setWrapText(true);
        logBox.getChildren().addAll(logLabel, logArea);
        VBox.setVgrow(logBox, Priority.ALWAYS);

        // Assemble left control pane
        leftPane.getChildren().addAll(controlsBox, new HBox(), logBox);
        leftPane.setPrefWidth(480);

        // Graph drawing area setup
        graphPane.setPrefSize(800, 700);
        graphPane.setStyle("-fx-background-color: #18181b; -fx-background-radius: 24;");

        // Add everything to main layout
        mainBox.getChildren().addAll(leftPane, graphPane);
        HBox.setHgrow(graphPane, Priority.ALWAYS);

        // Create and set up the scene
        Scene scene = new Scene(mainBox, 1300, 800);
        scene.getStylesheets().add(getClass().getResource("graphbfsapp.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        // Input shortcuts
        nodeField.setOnAction(e -> addNode());
        edgeFromField.setOnAction(e -> addEdge());
        edgeToField.setOnAction(e -> addEdge());

        // Responsive resizing
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            double newGraphWidth = newVal.doubleValue() - leftPane.getWidth();
            graphPane.setPrefWidth(Math.max(newGraphWidth, 200));
        });
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            graphPane.setPrefHeight(Math.max(newVal.doubleValue() - 40, 200));
        });

        // Ensure all UI elements stretch appropriately
        HBox.setHgrow(nodeField, Priority.ALWAYS);
        HBox.setHgrow(edgeFromField, Priority.ALWAYS);
        HBox.setHgrow(edgeToField, Priority.ALWAYS);
        VBox.setVgrow(sourceBox, Priority.ALWAYS);
        VBox.setVgrow(destBox, Priority.ALWAYS);
        VBox.setVgrow(logArea, Priority.ALWAYS);

        // Redraw graph on window resize
        graphPane.widthProperty().addListener((obs, oldVal, newVal) -> updateGraphView());
        graphPane.heightProperty().addListener((obs, oldVal, newVal) -> updateGraphView());
    }

    // Helper to create buttons with uniform styling
    private Button btn(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button b = new Button(text);
        b.setOnAction(handler);
        b.getStyleClass().add("action-btn");
        return b;
    }

    // Adds a node to the graph
    private void addNode() {
        String node = nodeField.getText().trim();
        if (node.isEmpty() || graph.hasNode(node)) {
            showError("Invalid or duplicate node name.");
            return;
        }
        graph.addNode(node);
        updateGraphView();
        updateNodeBoxes();
        nodeField.clear();
    }

    // Removes a node from the graph
    private void removeNode() {
        String node = nodeField.getText().trim();
        if (!graph.hasNode(node)) {
            showError("Node does not exist.");
            return;
        }
        graph.removeNode(node);
        updateGraphView();
        updateNodeBoxes();
        nodeField.clear();
    }

    // Adds an edge between two nodes
    private void addEdge() {
        String from = edgeFromField.getText().trim();
        String to = edgeToField.getText().trim();
        if (from.isEmpty() || to.isEmpty() || !graph.hasNode(from) || !graph.hasNode(to) || from.equals(to) || graph.isConnected(from, to)) {
            showError("Invalid edge or edge already exists.");
            return;
        }
        graph.addEdge(from, to);
        updateGraphView();
        edgeFromField.clear();
        edgeToField.clear();
    }

    // Removes an edge between two nodes
    private void removeEdge() {
        String from = edgeFromField.getText().trim();
        String to = edgeToField.getText().trim();
        if (!graph.hasNode(from) || !graph.hasNode(to) || !graph.isConnected(from, to)) {
            showError("Edge does not exist.");
            return;
        }
        graph.removeEdge(from, to);
        updateGraphView();
        edgeFromField.clear();
        edgeToField.clear();
    }

    // Triggers the BFS traversal and visual animation
    private void runBFS() {
        String src = sourceBox.getValue();
        String dst = destBox.getValue();
        if (src == null || dst == null || !graph.hasNode(src) || !graph.hasNode(dst)) {
            showError("Select valid source and destination nodes.");
            return;
        }
        bfsRouter = new BFSRouter(graph);
        List<String> path = bfsRouter.runBFS(src, dst);
        logArea.clear();
        bfsRouter.getLogs().forEach(log -> logArea.appendText(log + "\n"));
        animateTraversal(bfsRouter.getVisitedOrder(), path);
    }

    // Animates the BFS traversal step by step and highlights the shortest path
    private void animateTraversal(List<String> visited, List<String> path) {
        nodeCircles.values().forEach(c -> {
            c.setFill(Color.web("#23272e"));
            c.setStroke(Color.web("#bd976d"));
            c.setEffect(null);
        });

        Set<String> pathSet = path == null ? Collections.emptySet() : new HashSet<>(path);
        Timeline timeline = new Timeline();

        for (int i = 0; i < visited.size(); i++) {
            final int idx = i;
            KeyFrame kf = new KeyFrame(Duration.millis(ANIMATION_DELAY_MS * (i + 1)), e -> {
                for (int j = 0; j <= idx; j++) {
                    String v = visited.get(j);
                    Circle c = nodeCircles.get(v);
                    if (c != null) {
                        c.setFill(Color.web("#bd976d"));
                        c.setStroke(Color.web("#fff7e1"));
                        c.setEffect(new DropShadow(30, Color.web("#bd976d")));
                    }
                }
            });
            timeline.getKeyFrames().add(kf);
        }

        KeyFrame endFrame = new KeyFrame(Duration.millis(ANIMATION_DELAY_MS * (visited.size() + 1)), e -> {
            nodeCircles.values().forEach(c -> {
                c.setFill(Color.web("#23272e"));
                c.setStroke(Color.web("#bd976d"));
                c.setEffect(null);
            });
            for (String v : pathSet) {
                Circle c = nodeCircles.get(v);
                if (c != null) {
                    c.setFill(Color.web("#bd976d"));
                    c.setStroke(Color.web("#fff7e1"));
                    c.setEffect(new DropShadow(40, Color.web("#bd976d")));
                }
            }
        });
        timeline.getKeyFrames().add(endFrame);
        timeline.play();
    }

    // Updates combo boxes for source and destination nodes
    private void updateNodeBoxes() {
        List<String> nodes = new ArrayList<>(graph.getAllNodes());
        Collections.sort(nodes);
        sourceBox.getItems().setAll(nodes);
        destBox.getItems().setAll(nodes);
    }

    // Redraws the graph layout and nodes
    private void updateGraphView() {
        graphPane.getChildren().clear();
        nodeCircles.clear();
        nodeLabels.clear();

        List<String> nodes = new ArrayList<>(graph.getAllNodes());
        int n = nodes.size();
        double centerX = graphPane.getWidth() / 2;
        double centerY = graphPane.getHeight() / 2;
        double radius = Math.min(centerX, centerY) - 60;
        radius = Math.max(radius, 60);

        // Circular node positioning
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            graph.setNodePosition(nodes.get(i), x, y);
        }

        // Draw all edges
        for (String[] edge : graph.getAllEdges()) {
            java.awt.Point pf = graph.getNodePosition(edge[0]);
            java.awt.Point pt = graph.getNodePosition(edge[1]);
            if (pf != null && pt != null) {
                Line line = new Line(pf.x, pf.y, pt.x, pt.y);
                line.setStroke(Color.web("#a68a5b"));
                line.setStrokeWidth(2.5);
                graphPane.getChildren().add(line);
            }
        }

        // Draw all nodes
        for (String node : nodes) {
            java.awt.Point p = graph.getNodePosition(node);
            if (p == null) continue;
            Circle c = new Circle(p.x, p.y, NODE_RADIUS);
            c.setFill(Color.web("#23272e"));
            c.setStroke(Color.web("#bd976d"));
            c.setStrokeWidth(2.5);
            nodeCircles.put(node, c);
            graphPane.getChildren().add(c);

            Label lbl = new Label(node);
            lbl.setFont(Font.font("Segoe UI", 15));
            lbl.setStyle("-fx-text-fill: #f5f5f4; -fx-font-weight: bold;");
            lbl.setLayoutX(p.x - 12);
            lbl.setLayoutY(p.y - 12);
            nodeLabels.put(node, lbl);
            graphPane.getChildren().add(lbl);
        }
    }

    // Utility method to show error messages
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // Main method
    public static void main(String[] args) {
        launch(args);
    }
}
