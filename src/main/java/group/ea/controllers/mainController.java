package group.ea.controllers;

import group.ea.main;
import group.ea.structure.algorithm.Algorithm;
import group.ea.structure.algorithm.RLS;
import group.ea.structure.algorithm.SA;
import group.ea.structure.problem.OneMax;
import group.ea.structure.problem.LeadingOnes;
import group.ea.structure.problem.Problem;

import group.ea.structure.searchspace.BitString;
import group.ea.structure.searchspace.SearchSpace;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.chart.XYChart;


import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class mainController implements Initializable {
    @FXML
    private Button btnPlot, btnConnect, btnTable, createBlueprintBtn, loadBlueprintBtn;

    @FXML
    private BorderPane mainBorderPane,borderPane;
    @FXML
    private ChoiceBox<Integer> stringLength;
    @FXML
    private FlowPane flowPane;

    @FXML
    private CheckBox graphSelector,textSelector;

    @FXML
    private Label searchspaceLabel,problemLabel, algorithmLabel,criteriasLabel,timeLabel,mutationLabel, selectionLabel,crossoverLabel;

    @FXML
    Slider sliderSpeed;

    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
    LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
    public XYChart.Series<Number, Number> series = new XYChart.Series<>();

    private static AnimationTimer animationTimer;

    Algorithm algorithm;

    public volatile boolean isRunning = false;

    private Stage stage;
    private Scene scene;
    private Parent parent;
    private FileChooser fileChooser = new FileChooser();
    private String[] blueprintChoices = new String[5];

    double duration = 1000;
    Label titleLabel;
    VBox container = new VBox(5);

    private int bitStringValue;

    public mainController() {

       animationTimer = new AnimationTimer() {
           private long lastUpdate = 0;
           @Override
           public void handle(long l) {
               //algorithm.performSingleUpdate();
               if (l - lastUpdate >= duration) { // Update every second
                   if (!isAnimationPaused) {
                       if (graphSelector.isSelected()) {
                           algorithm.graphGraphics();
                       }
                       if (textSelector.isSelected()) {
                           algorithm.updateGraphics();
                       }
                       lastUpdate = l;
                       double speed = sliderSpeed.getValue();
                       duration = (TimeUnit.MILLISECONDS.toNanos(1000) * (1 - speed / sliderSpeed.getMax()));
                   }
               }
           }
       };
    }

    @FXML
    void createBlueprintHandler(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(Objects.requireNonNull(main.class.getResource("fxml/createBlueprintPage.fxml")));
        Scene scene = new Scene(root);
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        Platform.runLater(root::requestFocus);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void loadBlueprintHandler(ActionEvent event){
        fileChooser.showOpenDialog(stage);
    }


    @FXML
    void menuChangeHandler(ActionEvent event) throws IOException {
        if (event.getSource() == btnPlot){
            changeContent("plotPage");
        }
        else if (event.getSource() == btnConnect){
            changeContent("connectPage");
        }
        else if (event.getSource() == btnTable){
            changeContent("tablePage");
        }
    }

    @FXML
    void closeProgram(ActionEvent event) {
        Platform.exit();
    }

    private void changeContent(String page) throws IOException {
        Parent root = null;

        root = FXMLLoader.load(Objects.requireNonNull(main.class.getResource("fxml/" + page + ".fxml")));
        mainBorderPane.setCenter(root);
    }

    @FXML
    private Button startButton;
    @FXML
    public TextArea solutionArea = new TextArea();


    // Button action to start the EA
    int timesRun = 0;
    @FXML
    private void startEvolution() {
        if(!isAnimationPaused) {
            isRunning = true;
            solutionArea.clear();
            container.getChildren().clear();
            //startAlgorithm();
            //new Thread(this::runEvolution).start(); // Run EA in a separate thread
            SearchSpace searchSpace = null;
            if ("Bit strings".equals(blueprintChoices[0])) {
                bitStringValue = stringLength.getValue();
                searchSpace = new BitString(bitStringValue);
                System.out.println("is here");
            } else if ("Permutation".equals(blueprintChoices[0])) {
                //searchSpace = new Permutation(100);
            }

            Problem problem = null;
            if ("OneMax".equals(blueprintChoices[1])) {
                problem = new OneMax(searchSpace);
            } else if ("LeadingOnes".equals(blueprintChoices[1])) {
                problem = new LeadingOnes(searchSpace);
            }

            if (blueprintChoices[2].equals("RLS")) {
                algorithm = new RLS(searchSpace, problem, this);
            } else if (blueprintChoices[2].equals("Simulated Annealing")) {
                algorithm = new SA(searchSpace, problem, this);
            } else {
                algorithm = null;
            }
            if (algorithm != null) {
                timesRun++;
                //new Thread(() -> algorithm.runAlgorithm()).start();
                //algorithm.initialize();
                if (graphSelector.isSelected()) {
                    initializeChart();
                    if (!flowPane.getChildren().contains(lineChart)) {
                        flowPane.getChildren().add(lineChart);
                    }
                }

                // if you want to add each run to a new text area
            /*
            if (textSelector.isSelected()) {
                solutionArea = new TextArea();
                Label titleLabel = new Label("Run number: "+ (i));
                VBox container = new VBox(5);
                container.getChildren().addAll(titleLabel, solutionArea);
                //Optionally, style your label to make it look more like a title
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                    flowPane.getChildren().add(container);

            }
             */
                if (textSelector.isSelected()) {
                    titleLabel = new Label("Run number: " + (timesRun));
                    container.getChildren().addAll(titleLabel, solutionArea);
                    //Optionally, style your label to make it look more like a title
                    titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    if (!flowPane.getChildren().contains(container)) {
                        flowPane.getChildren().add(container);
                    }
                }

                startAlgorithm();
                algorithm.runAlgorithm();
            }
        } else {
            isAnimationPaused = false;
        }
        //new Thread(() -> algorithm.runAlgorithm()).start();
        // Running the algorithm
       // onePlusOneEA.runAlgorithm();
        //new Thread(this::runEvolution).start();
    }
    int i = 0;
    public void initializeChart() {
        xAxis.setLabel("Generation");
        yAxis.setLabel("Fitness");
        lineChart.setTitle("Fitness Chart");

        lineChart.setAnimated(true);
        xAxis.setAnimated(true);
        yAxis.setAnimated(true);
        System.out.println("height + " + lineChart.getHeight() + "width "+ lineChart.getWidth());
        series = new XYChart.Series<>();
        series.setName("Run number "+ (i+1));
        lineChart.getData().add(series);
        i++;
    }
    @FXML
    private void graphListener(ActionEvent event) {
        if (graphSelector.isSelected()) {
            if (!flowPane.getChildren().contains(lineChart)) {
                flowPane.getChildren().add(lineChart);
            }
        } else {
            flowPane.getChildren().remove(lineChart);
        }
    }
    @FXML
    private void textListener(ActionEvent event) {
        if (textSelector.isSelected()) {
            if (!flowPane.getChildren().contains(solutionArea)) {
                flowPane.getChildren().add(solutionArea);
            }
        } else {
            flowPane.getChildren().remove(solutionArea);
        }
    }

    public void recieveArray(String[] blueprintChoices) {
        this.blueprintChoices = blueprintChoices;
        searchspaceLabel.setText(blueprintChoices[0]);
        problemLabel.setText(blueprintChoices[1]);
        algorithmLabel.setText(blueprintChoices[2]);
        criteriasLabel.setText(blueprintChoices[3]);
    }
    public void stopEvolution() {
        stopGraphics();
    }
    public void updateCanvas() {
        algorithm.updateGraphics();
    }

    private void updateUIComponents() {
        // Logic to update other UI components like labels or tables
    }

    public void startAlgorithm() {
        // Initialize your algorithm runner here
        animationTimer.start(); // Start the animation
    }
    private boolean isAnimationPaused = false;// Starts paused
    @FXML
    private void pauseGraphics() {
        isAnimationPaused = true;
    }
    public void stopGraphics() {
        animationTimer.stop(); // Stop the animation
        isRunning = false; // Set running state to false to stop the algorithm
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        blueprintChoices[0] = "Bit strings";
        blueprintChoices[1] = "OneMax";
        blueprintChoices[2] = "RLS";
        blueprintChoices[3] = "Optimum reached";
        blueprintChoices[4] = "0.1";
        recieveArray(blueprintChoices);

        stringLength.getItems().addAll(10,100, 200, 300, 400, 500);
        stringLength.setValue(100);

    }
}