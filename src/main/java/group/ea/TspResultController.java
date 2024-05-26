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
/*
package group.ea;


import group.ea.structure.TSP.City;
import group.ea.structure.TSP.Solution;
import group.ea.structure.algorithm.AlgorithmUpdateListener;
import group.ea.structure.algorithm.TSPDATA;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.*;

public class TspResultController implements AlgorithmUpdateListener {

    private final boolean firstCall = true;
    private final Map<Edge, Line> edgeMap = new HashMap<>();
    ArrayList<TSPDATA> allSolutions;
    double speed;
    int maxY = 1200;
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
    private TSPDATA currentSolution;
    private TSPDATA nextSolution; // This represents the solution after mutation
    private Set<Edge> changedEdges; // To keep track of edges that have changed
    private Label fitnessLabel;
    private Label numberOfEdgesLabel;
    private Label gainLabel;
    private Label edgesDeletedLabel;
    private Label edgesAddedLabel;

    int edgesDeleted = 0;
    int edgesAdded = 0;
    public void initialize() {
        // Initialize controls
        pauseButton.setDisable(true);
        resetButton.setDisable(true);

        fitnessLabel = new Label("Fitness: ");
        numberOfEdgesLabel = new Label("Number of Edges: ");
        gainLabel = new Label("Gain: ");
        edgesDeletedLabel = new Label("Edges Deleted: 0");
        edgesAddedLabel = new Label("Edges Added: 0");

        VBox infoBox = new VBox(fitnessLabel, numberOfEdgesLabel, gainLabel, edgesDeletedLabel, edgesAddedLabel);
        infoBox.setSpacing(5);
        tspVisualization.getChildren().add(infoBox);

        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (timeline != null) {
                timeline.stop(); // Stop the timeline to reset the key frame duration
                double speed = newValue.doubleValue();
                KeyFrame keyFrame = new KeyFrame(Duration.seconds(1 / speed), event -> {
                    processQueue();
                });
                timeline.getKeyFrames().setAll(keyFrame); // Set the new key frame with the updated speed
                timeline.play(); // Restart the timeline with the new speed
            }
        });
    }



    private final Queue<TSPDATA> updateQueue = new LinkedList<>();

    public void setSolution(TSPDATA initialSolution) {
        this.currentSolution = initialSolution;
        this.nextSolution = initialSolution;// Copy initial solution
        this.changedEdges = new HashSet<>();
        //resetVisualization();
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

        //resetVisualization();
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        speed = speedSlider.getValue();

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1 / speed), event -> {
            processQueue();

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

    @Override
    public void firstSolution(Solution solution) {

        Solution firstSolution = solution;
        int numPoints = firstSolution.getListLength();

        // Draw the nodes
        for (int i = 0; i < numPoints; i++) {
            int x = firstSolution.getXSolution(i);
            int y = maxY - firstSolution.getYSolution(i);
            Circle circle = new Circle(x / 4, y / 4, 3, Color.RED);
            tspVisualization.getChildren().add(circle);
        }
        // Draw the edges
        for (int i = 0; i < numPoints; i++) {
            int x1 = firstSolution.getXSolution(i);
            int y1 = maxY - firstSolution.getYSolution(i);
            int x2 = firstSolution.getXSolution((i + 1) % numPoints);
            int y2 = maxY - firstSolution.getYSolution((i + 1) % numPoints);
            Line line = new Line(x1 / 4.0, y1 / 4.0, x2 / 4.0, y2 / 4.0);
           edgeMap.put(new Edge(x1, y1, x2, y2), line);
            tspVisualization.getChildren().add(line);

        }
        printEdgeMapDetails();

        }
    private void updateVisualization2() {
        System.out.println("called");
        // Clear previous visualization
        speed = speedSlider.getValue();
        tspVisualization.getChildren().clear();
        updateLabels();
        Solution firstSolution = currentSolution.solution;
        ArrayList<City> slSolution = currentSolution.slSolution;
        int numPoints = firstSolution.getListLength();

        // Draw the nodes
        for (int i = 0; i < numPoints; i++) {
            int x = firstSolution.getXSolution(i);
            int y = maxY - firstSolution.getYSolution(i);
            Circle circle = new Circle(x / 4, y / 4, 3, Color.RED);
            tspVisualization.getChildren().add(circle);
        }
        // Draw the edges
        for (int i = 0; i < numPoints; i++) {
            int x1 = (int) slSolution.get(i).getX();
            int y1 =  (maxY - (int) slSolution.get(i).getY());
            int x2 = (int) slSolution.get((i + 1) % numPoints).getX();
            int y2 =  (maxY - (int) slSolution.get((i + 1) % numPoints).getY());
            Line line = new Line(x1 / 4.0, y1 / 4.0, x2 / 4.0, y2 / 4.0);
            edgeMap.put(new Edge(x1, y1, x2, y2), line);
            tspVisualization.getChildren().add(line);

        }

    }

    private void updateVisualization() {
        // Clear previous visualization
        speed = speedSlider.getValue();
        //removed edges
        int x1 = (int) currentSolution.X1.getX();
        int y1 = maxY - (int) currentSolution.X1.getY();
        int x2 = (int) currentSolution.X2.getX();
        int y2 = maxY - (int) currentSolution.X2.getY();
        int x3 = (int) currentSolution.X3.getX();
        int y3 = maxY - (int) currentSolution.X3.getY();
        int x4 = (int) currentSolution.X4.getX();
        int y4 = maxY - (int) currentSolution.X4.getY();
        List<Edge> newEdges = new ArrayList<>();

        Edge edge1 = new Edge(x1, y1, x2, y2);
        Edge edge2 = new Edge(x2, y2, x1, y1);
        Edge edge3 = new Edge(x3, y3, x4, y4);
        Edge edge4 = new Edge(x4, y4, x3, y3);

        Line line1 = edgeMap.get(edge1);
        Line line2 = edgeMap.get(edge2);
        Line line3 = edgeMap.get(edge3);
        Line line4 = edgeMap.get(edge4);

        if (line1 != null) {
            tspVisualization.getChildren().remove(line1);
            edgeMap.remove(edge1);
            edgesDeleted++;
        }

        if (line2 != null) {
            tspVisualization.getChildren().remove(line2);
            edgeMap.remove(edge2);
            edgesDeleted++;
        }
        if (line3 != null) {
            tspVisualization.getChildren().remove(line3);
            edgeMap.remove(edge3);
            edgesDeleted++;
        }
        if (line4 != null) {
            tspVisualization.getChildren().remove(line4);
            edgeMap.remove(edge4);
            edgesDeleted++;
        }
        if(line1 == null && line2 == null || line3 == null && line4 == null){
            if(line1 == null) {
                System.out.println("Line1 not found for edge: " + new Edge(x1, y1, x2, y2));
            }
            if(line2 == null) {
                System.out.println("Line2 not found for edge: " + new Edge(x2, y2, x1, y1));
            }
            if(line3 == null) {
                System.out.println("Line3 not found for edge: " + new Edge(x3, y3, x4, y4));
            }
            if(line4 == null) {
                System.out.println("Line4 not found for edge: " + new Edge(x4, y4, x3, y3));
            }
            System.out.println(currentSolution.opt3 + " 3 opt");
            System.out.println("Line4 not found for edge: " + new Edge(x4, y4, x3, y3));
            System.out.println("deleted 1 and 2"+ new Edge(x1,y1,x2,y2) + " "+ new Edge(x3,y3,x4,y4));

            printEdgeMapDetails();
        }



        if (currentSolution.opt3) {
            // X -> X+1  Y -> Y+1 Z -> Z + 1
            int x5 = (int) currentSolution.X5.getX();
            int y5 = maxY - (int) currentSolution.X5.getY();
            int x6 = (int) currentSolution.X6.getX();
            int y6 = maxY - (int) currentSolution.X6.getY();
            System.out.println("X1 "+ x1 + " Y1 " + y1 + " X2 " + x2 + " Y2 " + y2);
            System.out.println("X3 "+ x3 + " Y3 " + y3 + " X4 " + x4 + " Y4 " + y4);
            System.out.println("X5 "+ x5 + " Y5 " + y5 + " X6 " + x6 + " Y6 " + y6);
            Edge edge5 = new Edge(x5, y5, x6, y6);
            Edge edge6 = new Edge(x6, y6, x5, y5);
            Line line5 = edgeMap.get(edge5);
            Line line6 = edgeMap.get(edge6);

            if (line5 != null) {
                tspVisualization.getChildren().remove(line5);
                edgeMap.remove(new Edge(x5, y5, x6, y6));
                edgesDeleted++;
            }
            if (line6 != null) {
                tspVisualization.getChildren().remove(line6);
                edgeMap.remove(new Edge(x6, y6, x5, y5));
                edgesDeleted++;
            }
            if(line5 == null && line6 == null){
                System.out.println("Line6 not found for edge: " + new Edge(x5, y5, x6, y6));
                System.out.println("deleted 1 and 2"+ new Edge(x1,y1,x2,y2) + " "+ new Edge(x3,y3,x4,y4));

                printEdgeMapDetails();
            }

            switch (currentSolution.optCase) {
                //x1x2, z1z2, y1,y2
                //x1x2, x3x4 ,x5x6
                // X1  Y Z
                // I J K
                case 1:
                    // i -> k and i + 1 -> k + 1 and remain j -> j +1
                    newEdges.add(new Edge(x1, y1, x5, y5));
                    newEdges.add(new Edge(x2, y2, x6, y6));
                    newEdges.add(new Edge(x3, y3, x4, y4));
                    //new edges added:
                    System.out.println("Case 1");
                    System.out.println("x1: " + x1 + " y1: " + y1 + " x5: " + x5 + " y5: " + y5);
                    System.out.println("x2: " + x2 + " y2: " + y2 + " x6: " + x6 + " y6: " + y6);
                    System.out.println("x3: " + x3 + " y3: " + y3 + " x4: " + x4 + " y4: " + y4);

                    break;
                case 2:
                    // j -> k and j + 1 -> k + 1 and remain i-> i +1
                    newEdges.add(new Edge(x3, y3, x5, y5));
                    newEdges.add(new Edge(x4, y4, x6, y6));
                    newEdges.add(new Edge(x1, y1, x2, y2));
                    System.out.println("Case 2");
                    System.out.println("x3: " + x3 + " y3: " + y3 + " x5: " + x5 + " y5: " + y5);
                    System.out.println("x4: " + x4 + " y4: " + y4 + " x6: " + x6 + " y6: " + y6);
                    System.out.println("x1: " + x1 + " y1: " + y1 + " x2: " + x2 + " y2: " + y2);
                    break;
                case 3:
                    // i -> j and i + 1 -> j +1 and remain k -> k +1
                    newEdges.add(new Edge(x1, y1, x3, y3));
                    newEdges.add(new Edge(x2, y2, x4, y4));
                    newEdges.add(new Edge(x5, y5, x6, y6));
                    System.out.println("Case 3");
                    System.out.println("x1: " + x1 + " y1: " + y1 + " x3: " + x3 + " y3: " + y3);
                    System.out.println("x2: " + x2 + " y2: " + y2 + " x4: " + x4 + " y4: " + y4);
                    System.out.println("x5: " + x5 + " y5: " + y5 + " x6: " + x6 + " y6: " + y6);


                    break;
                case 4:
                    // i -> j and i + 1 -> k
                    // k +1 -> j +1
                    newEdges.add(new Edge(x1, y1, x3, y3));
                    newEdges.add(new Edge(x2, y2, x5, y5));
                    newEdges.add(new Edge(x6, y6, x4, y4));
                    System.out.println("Case 4");
                    System.out.println("x1: " + x1 + " y1: " + y1 + " x3: " + x3 + " y3: " + y3);
                    System.out.println("x2: " + x2 + " y2: " + y2 + " x5: " + x5 + " y5: " + y5);
                    System.out.println("x6: " + x6 + " y6: " + y6 + " x4: " + x4 + " y4: " + y4);
                    break;
                case 5:
                    newEdges.add(new Edge(x1, y1, x5, y5));
                    newEdges.add(new Edge(x2, y2, x4, y4));
                    newEdges.add(new Edge(x3, y3, x6, y6));
                    System.out.println("Case 5");
                    System.out.println("x1: " + x1 + " y1: " + y1 + " x5: " + x5 + " y5: " + y5);
                    System.out.println("x2: " + x2 + " y2: " + y2 + " x4: " + x4 + " y4: " + y4);
                    System.out.println("x3: " + x3 + " y3: " + y3 + " x6: " + x6 + " y6: " + y6);
                    break;
                case 6:
                    newEdges.add(new Edge(x1, y1, x4, y4));
                    newEdges.add(new Edge(x2, y2, x6, y6));
                    newEdges.add(new Edge(x3, y3, x5, y5));
                    System.out.println("Case 6");
                    System.out.println("x1: " + x1 + " y1: " + y1 + " x4: " + x4 + " y4: " + y4);
                    System.out.println("x2: " + x2 + " y2: " + y2 + " x6: " + x6 + " y6: " + y6);
                    System.out.println("x3: " + x3 + " y3: " + y3 + " x5: " + x5 + " y5: " + y5);

                    break;
                case 7:
                    newEdges.add(new Edge(x1, y1, x4, y4));
                    newEdges.add(new Edge(x2, y2, x5, y5));
                    newEdges.add(new Edge(x3, y3, x6, y6));
                    System.out.println("Case 7");
                    System.out.println("x1: " + x1 + " y1: " + y1 + " x4: " + x4 + " y4: " + y4);
                    System.out.println("x2: " + x2 + " y2: " + y2 + " x5: " + x5 + " y5: " + y5);
                    System.out.println("x3: " + x3 + " y3: " + y3 + " x6: " + x6 + " y6: " + y6);
                    break;
                default:
                    break;

            }

    }else {

            //draw the new edges
            Line newLine1 = new Line(x1 / 4.0, y1 / 4.0, x3 / 4.0, y3 / 4.0);
            Line newLine2 = new Line(x2 / 4.0, y2 / 4.0, x4 / 4.0, y4 / 4.0);
            edgesAdded++;
            edgesAdded++;

            newLine1.setStroke(Color.GREEN);
            newLine2.setStroke(Color.GREEN);
            tspVisualization.getChildren().add(newLine1);
            tspVisualization.getChildren().add(newLine2);
            edgeMap.put(new Edge(x1, y1, x3, y3), newLine1);
            edgeMap.put(new Edge(x2, y2, x4, y4), newLine2);
        }
        for (Edge edge : newEdges) {
            Line newLine = new Line(edge.x1 / 4.0, edge.y1 / 4.0, edge.x2 / 4.0, edge.y2 / 4.0);
            newLine.setStroke(Color.GREEN);
            tspVisualization.getChildren().add(newLine);
            edgeMap.put(edge, newLine);
            edgesAdded++;
        }
        updateLabels();



    }
    private void printEdgeMapDetails() {
        System.out.println("Current edges in edgeMap:");
        for (Map.Entry<Edge, Line> entry : edgeMap.entrySet()) {
            Edge edge = entry.getKey();
            Line line = entry.getValue();
            System.out.println("Edge: (" + edge.x1 + ", " + edge.y1 + ") -> (" + edge.x2 + ", " + edge.y2 + ")");
        }
    }
    private void updateLabels() {
        fitnessLabel.setText("Fitness: " + currentSolution.fitness);
        numberOfEdgesLabel.setText("Number of Edges: " + edgeMap.size());
        gainLabel.setText("Gain: " + currentSolution.improvement);
        edgesDeletedLabel.setText("Edges Deleted: " + edgesDeleted);
        edgesAddedLabel.setText("Edges Added: " + edgesAdded);
    }

    @Override
    public void tspGraphics(ArrayList<TSPDATA> solution) {
        allSolutions = solution;
    }

    @Override
    public void receiveUpdate(TSPDATA solution){
        updateQueue.add(solution);
    }

    private void processQueue() {
        if (!updateQueue.isEmpty()) {
            TSPDATA nextSolution = updateQueue.poll();
            setSolution(nextSolution);
            updateVisualization();
        }
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
        @Override
        public String toString() {
            return "Edge{" +
                    "x1=" + x1 +
                    ", y1=" + y1 +
                    ", x2=" + x2 +
                    ", y2=" + y2 +
                    '}';
        }
    }
}
*/
/*
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
                        circle.setFill(Color.DARKBLUE);
                        tspVisualization.getChildren().add(circle);
                        if (j > 0) { // Draw line from the previous point to the current point
                            Line line = new Line(prevX / 4.0, prevY / 4.0, x / 4.0, y / 4.0);
                            line.setStroke(Color.BLACK);
                            line.setStrokeWidth(2);
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
                    line.setStroke(Color.BLACK);
                    line.setStrokeWidth(2);
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
*/