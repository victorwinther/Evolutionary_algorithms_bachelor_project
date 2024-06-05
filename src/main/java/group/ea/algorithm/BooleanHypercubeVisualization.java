package group.ea.algorithm;

import group.ea.controllers.mainController;
import group.ea.problem.Problem;
import group.ea.searchspace.SearchSpace;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.ArrayList;


public class BooleanHypercubeVisualization {
    private static final double X_SCALE = 0.8;
    protected final mainController _mainController;
    public boolean isDone = false;
    public Pane hypercubePane;
    double fixedWidth = 600.0;
    double fixedHeight = 400.0;
    Problem problem;
    SearchSpace searchSpace;
    private int currentWidth = 600; // Example width
    private int currentHeight = 400; // Example height
    private ArrayList<Circle> pointsList = new ArrayList<>();
    private int runNumber;
    // Calculate the y-coordinate based on the number of 1-bits

    public BooleanHypercubeVisualization(SearchSpace searchSpace, Problem problem, mainController controller, Pane hypercubenPane,int runNumber) {
        this.searchSpace = searchSpace;
        this.problem = problem;
        _mainController = controller;
        this.hypercubePane = hypercubenPane;
        hypercubePane.setPrefSize(fixedWidth, fixedHeight);
        addRunNumber(runNumber);

// Set min and max size to ensure the size stays fixed
        hypercubePane.setMinSize(fixedWidth, fixedHeight);
        hypercubePane.setMaxSize(fixedWidth, fixedHeight);
        hypercubePane.getStyleClass().add("pane-border");
        drawSearchSpace();

    }
    private void addRunNumber(int runNumber) {
        hypercubePane.getChildren().clear();
        this.runNumber = runNumber;
        Label runLabel = new Label("Run " + runNumber);
        runLabel.setLayoutX(10);
        runLabel.setLayoutY(10);
        hypercubePane.getChildren().add(runLabel);

    }

    private static int calculateYCoordinate(String bitString) {
        int count = 0;
        for (char bit : bitString.toCharArray()) {
            if (bit == '1') {
                count++;
            }
        }
        return count;
    }

    // Calculate the x-coordinate based on the positions of 1-bits
    private static int calculateXCoordinate(String bitString) {
        int sum = 0;
        for (int i = 0; i < bitString.length(); i++) {
            if (bitString.charAt(i) == '1') {
                sum += i + 1; // Adding 1 to make it 1-indexed for weighting
            }
        }
        return sum;
    }

    private Path plotFunction(double offsetX, double offsetY, int rotationDegrees) {
        Path path = new Path();
        boolean start = true;

        for (double x = -7; x <= 7; x += 0.1) {
            double y = Math.exp(-(x * x) / 8);

            double scaledX = x * 25;
            double scaledY = -y * 250; // Invert y to match JavaFX coordinate system

            // Apply rotation adjustments
            double adjustedX = scaledX, adjustedY = scaledY;
            if (rotationDegrees == 90) {
                // 90 degrees rotation
                adjustedX = scaledY + offsetX;
                adjustedY = scaledX + offsetY;
            } else if (rotationDegrees == 270) {
                // 270 degrees rotation (90 degrees counterclockwise)
                adjustedX = -scaledY + offsetX;
                adjustedY = -scaledX + offsetY;
            }

            if (start) {
                path.getElements().add(new MoveTo(adjustedX, adjustedY));
                start = false;
            } else {
                path.getElements().add(new LineTo(adjustedX, adjustedY));
            }
        }

        path.setStroke(Color.BLACK);
        path.setStrokeWidth(2);
        path.setFill(null);

        return path;
    }

    private int getXDeviation(int y, int bitStringLength) {
        //return (int) (Math.sin(Math.PI * (y / (double)currentHeight)) * getXScale());
        double yHeight = (double) y / bitStringLength * 2 * 7 - 7;
        int function = (int) (Math.exp(-(Math.pow(yHeight, 2) / 8)) * 250);
        return function;
    }

    private double getXScale() {
        return currentWidth * (25 / 2);
    }

    private void drawSearchSpace() {

        // 90 degrees rotated function plot
        Path rotated90Plot = plotFunction((double) currentWidth / 2, (double) currentHeight / 2, 90);
        hypercubePane.getChildren().add(rotated90Plot);

        // 270 degrees rotated function plot (effectively 90 degrees counterclockwise)
        Path rotated270Plot = plotFunction((double) currentWidth / 2, (double) currentHeight / 2, 270);
        hypercubePane.getChildren().add(rotated270Plot);
        //hypercubePane.setStyle("-fx-border-color: black; -fx-border-width: 2;");
    }

    public Circle getDisplayCoordinates(String bitString, boolean isPerfectSolution) {

        int onemax = 0;
        for (char bit : bitString.toCharArray()) {
            if (bit == '1') {
                onemax++;
            }
        }

        // Calculate the x-coordinate based on the positions of 1-bits
        int sumOfIndices = 0;
        for (int i = 0; i < bitString.length(); i++) {
            if (bitString.charAt(i) == '1') {
                sumOfIndices += i; //
            }
        }
        // the minimal and maximal values are computed with Gaussian sums.
        int minimalSumOfIndices = ((onemax - 1) * onemax) / 2;
        int maximalSumOfIndices = ((bitString.length() - 1) * bitString.length()) / 2 - ((bitString.length() - 1 - onemax) * (bitString.length() - onemax)) / 2;
        int range = maximalSumOfIndices - minimalSumOfIndices;

        // compute a normalized value in [-range, +range]
        int x = 2 * sumOfIndices - 2 * minimalSumOfIndices - range;

        int centerX = currentWidth / 2;
        int yOffset = (int) (currentHeight / 2 + 7 * 25 - ((double) onemax / bitString.length() * 7.0 * 25.0 * 2.0));

        int xOffset = 0;
        if (range != 0) {
            xOffset = (int) ((x * getXDeviation(onemax, bitString.length())) / (double) range);
        }
        if (isPerfectSolution) {
            Circle circle = new Circle(centerX + xOffset, yOffset, 5);
            circle.setFill(Color.RED);
            isDone = true;
            return circle;

        } else {
            Circle mainCircle = new Circle(centerX + xOffset, yOffset, 4);
            mainCircle.setFill(Color.BLUE);
            return mainCircle;
        }
    }

    private void drawPixel(Pane pane, int x, int y) {
        //Line line = new Line(x - 1, y, x + 1, y);
        Circle point = new Circle(x, y, 1); // x and y are the coordinates, radius is 2
        point.setFill(Color.BLUE); // Color of the point
        //pane.getChildren().add(line);
        pane.getChildren().add(point);
    }

    public Circle getNextCircle() {
        if (!pointsList.isEmpty()) {
            return pointsList.remove(0);
        }
        return null;
    }

}
