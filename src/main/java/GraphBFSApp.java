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

public class GraphBFSApp extends Application {
    private final Graph graph = new Graph();
    private BFSRouter bfsRouter = new BFSRouter(graph);

    private final Pane graphPane = new Pane();
    private final TextArea logArea = new TextArea();
    private final ComboBox<String> sourceBox = new ComboBox<>();
    private final ComboBox<String> destBox = new ComboBox<>();
    private final TextField nodeField = new TextField();
    private final TextField edgeFromField = new TextField();
    private final TextField edgeToField = new TextField();
    private static final int NODE_RADIUS = 22;
    private static final int ANIMATION_DELAY_MS = 400;
    private final Map<String, Circle> nodeCircles = new HashMap<>();
    private final Map<String, Label> nodeLabels = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Graph BFS Visualizer");

        nodeField.setPromptText("Node name");
        edgeFromField.setPromptText("From node");
        edgeFromField.setPrefWidth(120);
        edgeToField.setPromptText("To node");
        edgeToField.setPrefWidth(120);

        HBox mainBox = new HBox();
        mainBox.setId("mainBox");
        mainBox.setSpacing(0);
        mainBox.setFillHeight(true);

        VBox leftPane = new VBox();
        leftPane.setId("leftPane");
        leftPane.setSpacing(32);
        leftPane.setFillWidth(true);
        leftPane.setPrefWidth(480);

        VBox controlsBox = new VBox();
        controlsBox.setId("controlsBox");
        controlsBox.setSpacing(22);

        Label nodeLabel = new Label("Node");
        nodeLabel.setLabelFor(nodeField);
        HBox nodeInputBox = new HBox(10, nodeField, btn("Add", e -> addNode()), btn("Remove", e -> removeNode()));
        VBox nodeBox = new VBox(4, nodeLabel, nodeInputBox);

        Label edgeLabel = new Label("Edge");
        edgeLabel.setLabelFor(edgeFromField);
        HBox edgeInputBox = new HBox(10, edgeFromField, edgeToField, btn("Add", e -> addEdge()), btn("Remove", e -> removeEdge()));
        VBox edgeBox = new VBox(4, edgeLabel, edgeInputBox);

        VBox sourceBoxContainer = new VBox(4, new Label("Source"), sourceBox);
        VBox destBoxContainer = new VBox(4, new Label("Destination"), destBox);
        HBox srcDestBox = new HBox(22, sourceBoxContainer, destBoxContainer);

        Button bfsBtn = btn("Run BFS", e -> runBFS());
        bfsBtn.setId("runBFSBtn");
        bfsBtn.setPrefSize(240, 54);
        bfsBtn.setStyle(bfsBtn.getStyle() + ";-fx-font-size: 20px; -fx-font-weight: 800;");

        controlsBox.getChildren().addAll(nodeBox, edgeBox, srcDestBox, bfsBtn);

        HBox bfsQueueBox = new HBox();
        bfsQueueBox.setId("bfsQueueBox");
        bfsQueueBox.setSpacing(8);

        VBox logBox = new VBox();
        logBox.setId("logBox");
        logBox.setSpacing(8);
        Label logLabel = new Label("BFS Log:");
        logLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #ffe7b2;");
        logArea.setEditable(false);
        logArea.setPrefHeight(220);
        logArea.setWrapText(true);
        logBox.getChildren().addAll(logLabel, logArea);
        VBox.setVgrow(logBox, Priority.ALWAYS);

        leftPane.getChildren().addAll(controlsBox, bfsQueueBox, logBox);

        graphPane.setPrefSize(800, 700);
        graphPane.setStyle("-fx-background-color: #18181b; -fx-background-radius: 24; -fx-border-radius: 24;");
        HBox.setHgrow(graphPane, Priority.ALWAYS);

        mainBox.getChildren().addAll(leftPane, graphPane);

        Scene scene = new Scene(mainBox, 1300, 800);
        scene.getStylesheets().add(getClass().getResource("graphbfsapp.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        nodeField.setOnAction(e -> addNode());
        edgeFromField.setOnAction(e -> addEdge());
        edgeToField.setOnAction(e -> addEdge());
    }

    private Button btn(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button b = new Button(text);
        b.setOnAction(handler);
        b.getStyleClass().add("action-btn");
        return b;
    }

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



    private void animateTraversal(List<String> visited, List<String> path) {
        // Reset all nodes
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
                // Highlight all visited nodes up to and including idx
                for (int j = 0; j <= idx; j++) {
                    String v = visited.get(j);
                    Circle c = nodeCircles.get(v);
                    if (c != null) {
                        c.setFill(Color.web("#bd976d"));
                        c.setStroke(Color.web("#fff7e1"));
                        DropShadow glow = new DropShadow(30, Color.web("#bd976d"));
                        c.setEffect(glow);
                    }
                }
            });
            timeline.getKeyFrames().add(kf);
        }

        // At the end, highlight only the shortest path
        KeyFrame endFrame = new KeyFrame(Duration.millis(ANIMATION_DELAY_MS * (visited.size() + 1)), e -> {
            // Reset all nodes first
            nodeCircles.values().forEach(c -> {
                c.setFill(Color.web("#23272e"));
                c.setStroke(Color.web("#bd976d"));
                c.setEffect(null);
            });
            // Highlight only the path
            for (String v : pathSet) {
                Circle c = nodeCircles.get(v);
                if (c != null) {
                    c.setFill(Color.web("#bd976d"));
                    c.setStroke(Color.web("#fff7e1"));
                    DropShadow glow = new DropShadow(40, Color.web("#bd976d"));
                    c.setEffect(glow);
                }
            }
        });
        timeline.getKeyFrames().add(endFrame);
        timeline.play();
    }

    private void updateNodeBoxes() {
        List<String> nodes = new ArrayList<>(graph.getAllNodes());
        Collections.sort(nodes);
        sourceBox.getItems().setAll(nodes);
        destBox.getItems().setAll(nodes);
    }

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

        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            graph.setNodePosition(nodes.get(i), x, y);
        }

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

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
