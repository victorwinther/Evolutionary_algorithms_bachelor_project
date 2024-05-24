/*package group.ea;

import group.ea.structure.TSP.Solution;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.Set;

public class TspResultController {

    @FXML
    private Pane tspVisualization;
    @FXML
    private Slider speedSlider;
    @FXML
    private Button startButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button resetButton;

    private Timeline timeline;
    private boolean isPaused = false;
    private int currentStep = 0;

    private Solution currentSolution;
    private Solution nextSolution; // This represents the solution after mutation
    private Set<Edge> changedEdges; // To keep track of edges that have changed

    public void initialize() {
        // Initialize controls
        pauseButton.setDisable(true);
        resetButton.setDisable(true);
    }

    public void setSolution(Solution initialSolution) {
        this.currentSolution = initialSolution;
        this.nextSolution = initialSolution;// Copy initial solution
        this.changedEdges = new HashSet<>();
        resetVisualization();
    }

    @FXML
    private void startVisualization() {
        if (isPaused) {
            timeline.play();
            isPaused = false;
            pauseButton.setDisable(false);
            startButton.setDisable(true);
            return;
        }

        resetVisualization();
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        double speed = speedSlider.getValue();

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1 / speed), event -> {
            updateVisualization();
            applyMutation(); // Apply mutation to generate next solution
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();

        startButton.setDisable(true);
        pauseButton.setDisable(false);
        resetButton.setDisable(false);
    }

    @FXML
    private void pauseVisualization() {
        if (timeline != null) {
            timeline.pause();
            isPaused = true;
            startButton.setDisable(false);
            pauseButton.setDisable(true);
        }
    }

    @FXML
    private void resetVisualization() {
        if (timeline != null) {
            timeline.stop();
        }
        tspVisualization.getChildren().clear();
        currentStep = 0;
        startButton.setDisable(false);
        pauseButton.setDisable(true);
        resetButton.setDisable(true);
        isPaused = false;
    }

    private void updateVisualization() {
        // Clear previous visualization
        tspVisualization.getChildren().clear();

        int maxY = 1200;
        int numPoints = currentSolution.getListLength();

        // Draw the nodes
        for (int i = 0; i < numPoints; i++) {
            int x = currentSolution.getXSolution(i);
            int y = maxY - currentSolution.getYSolution(i);
            Circle circle = new Circle(x / 4, y / 4, 3, Color.RED);
            tspVisualization.getChildren().add(circle);
        }

        // Draw the edges
        for (int i = 0; i < numPoints; i++) {
            int x1 = currentSolution.getXSolution(i);
            int y1 = maxY - currentSolution.getYSolution(i);
            int x2 = currentSolution.getXSolution((i + 1) % numPoints);
            int y2 = maxY - currentSolution.getYSolution((i + 1) % numPoints);
            Line line = new Line(x1 / 4.0, y1 / 4.0, x2 / 4.0, y2 / 4.0);

            // Check if the edge has changed
            Edge edge = new Edge(x1, y1, x2, y2);
            if (changedEdges.contains(edge)) {
                line.setStroke(Color.GREEN);
            } else {
                line.setStroke(Color.BLUE);
            }
            tspVisualization.getChildren().add(line);
        }

        // Draw changed edges in red
        for (Edge edge : changedEdges) {
            Line line = new Line(edge.x1 / 4.0, edge.y1 / 4.0, edge.x2 / 4.0, edge.y2 / 4.0);
            line.setStroke(Color.RED);
            tspVisualization.getChildren().add(line);
        }

        // Prepare for the next iteration
        Solution temp = currentSolution;
        currentSolution = nextSolution;
        nextSolution = temp;
    }

    private void applyMutation() {
        // Example mutation logic: swap two random points
        int numPoints = nextSolution.getListLength();
        int index1 = (int) (Math.random() * numPoints);
        int index2 = (int) (Math.random() * numPoints);

        int x1 = nextSolution.getXSolution(index1);
        int y1 = nextSolution.getYSolution(index1);
        int x2 = nextSolution.getXSolution(index2);
        int y2 = nextSolution.getYSolution(index2);

        // Track the edges that will change
        changedEdges.clear();
        changedEdges.add(new Edge(nextSolution.getXSolution(index1), nextSolution.getYSolution(index1), nextSolution.getXSolution((index1 + 1) % numPoints), nextSolution.getYSolution((index1 + 1) % numPoints)));
        changedEdges.add(new Edge(nextSolution.getXSolution(index1), nextSolution.getYSolution(index1), nextSolution.getXSolution((index1 - 1 + numPoints) % numPoints), nextSolution.getYSolution((index1 - 1 + numPoints) % numPoints)));
        changedEdges.add(new Edge(nextSolution.getXSolution(index2), nextSolution.getYSolution(index2), nextSolution.getXSolution((index2 + 1) % numPoints), nextSolution.getYSolution((index2 + 1) % numPoints)));
        changedEdges.add(new Edge(nextSolution.getXSolution(index2), nextSolution.getYSolution(index2), nextSolution.getXSolution((index2 - 1 + numPoints) % numPoints), nextSolution.getYSolution((index2 - 1 + numPoints) % numPoints)));

        // Apply the mutation
        nextSolution.setXSolution(index1, x2);
        nextSolution.setYSolution(index1, y2);
        nextSolution.setXSolution(index2, x1);
        nextSolution.setYSolution(index2, y1);
    }

    // Helper class to track edges
    private static class Edge {
        int x1, y1, x2, y2;

        Edge(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Edge edge = (Edge) o;

            if (x1 != edge.x1) return false;
            if (y1 != edge.y1) return false;
            if (x2 != edge.x2) return false;
            return y2 == edge.y2;
        }

        @Override
        public int hashCode() {
            int result = x1;
            result = 31 * result + y1;
            result = 31 * result + x2;
            result = 31 * result + y2;
            return result;
        }
    }
}

 */
package group.ea;

import group.ea.structure.TSP.City;
import group.ea.structure.TSP.Solution;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;

public class TspResultController {

    @FXML
    private FlowPane mainFlowPane;

    public void tspGraphics(Solution _sl) {
            
            Platform.runLater(() -> {
                Pane tspVisualization = new Pane();
                tspVisualization.setPrefSize(400, 300); // Set preferred size for the visualization pane

                int maxY = 1200; // Replace with the actual maximum Y value of your canvas

                boolean showGraph = true; // Replace with your actual condition
                if (showGraph) {
                    int firstX = 0, firstY = 0;
                    int prevX = 0;
                    int prevY = 0;
                    for (int j = 0; j < _sl.getListLength(); j++) {

                        System.out.println("i er " + j + " og listlength er " + _sl.getListLength());
                        int x = _sl.getXSolution(j);
                        int y = maxY - _sl.getYSolution(j); // Subtract the y-coordinate from maxY to mirror it
                        Circle circle = new Circle(x / 4, y / 4, 3);
                        circle.setFill(Color.RED);
                        tspVisualization.getChildren().add(circle);
                        if (j > 0) { // Draw line from the previous point to the current point
                            Line line = new Line(prevX / 4.0, prevY / 4.0, x / 4.0, y / 4.0);
                            line.setStroke(Color.BLUE);
                            tspVisualization.getChildren().add(line);
                        } else {
                            firstX = x;
                            firstY = y;
                        }

                        prevX = x;
                        prevY = y;
                        System.out.println(x + " x og er y" + y);
                    }
                    Line line = new Line(prevX / 4.0, prevY / 4.0, firstX / 4.0, firstY / 4.0);
                    line.setStroke(Color.BLUE);
                    tspVisualization.getChildren().add(line);

                    // Add a label to display the fitness
                    Label fitnessLabel = new Label("Fitness: " + _sl.computeFitness());
                    fitnessLabel.setTextFill(Color.BLACK);
                    fitnessLabel.setLayoutX(10);
                    fitnessLabel.setLayoutY(10);
                    tspVisualization.getChildren().add(fitnessLabel);
                }

                mainFlowPane.getChildren().add(tspVisualization); // Add the new visualization pane to the flow pane
            });

    }
}
