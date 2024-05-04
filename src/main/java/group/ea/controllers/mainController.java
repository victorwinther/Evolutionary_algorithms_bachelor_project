package group.ea.controllers;

import group.ea.main;
import group.ea.structure.TSP.Solution;
import group.ea.structure.TSP.TSPParser;
import group.ea.structure.algorithm.*;
import group.ea.structure.algorithm.BooleanHypercubeVisualization;
import group.ea.structure.algorithm.Algorithm;
import group.ea.structure.algorithm.RLS;
import group.ea.structure.algorithm.SA;
import group.ea.structure.algorithm.onePlusOneEA;
import group.ea.structure.problem.OneMax;
import group.ea.structure.problem.LeadingOnes;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.BitString;
import group.ea.structure.searchspace.SearchSpace;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.chart.XYChart;


import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class mainController implements Initializable {
    private static AnimationTimer animationTimer;
    public final NumberAxis xAxis = new NumberAxis();
    public final NumberAxis yAxis = new NumberAxis();
    private final FileChooser fileChooser = new FileChooser();
    @FXML
    public FlowPane flowPane;
    @FXML
    public Slider generationSlider;
    public XYChart.Series<Number, Number> series = new XYChart.Series<>();
    public volatile boolean isRunning = false;
    public BooleanHypercubeVisualization booleanHypercubeVisualization;
    public Pane hypercubenPane = new Pane();

    public Pane tspVisualization = new Pane();
    @FXML
    public TextArea solutionArea = new TextArea();
    @FXML
    Slider sliderSpeed;
    LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
    double duration = 1000;
    Label titleLabel;
    VBox container = new VBox(5);
    // Button action to start the EA
    int timesRun = 0;
    int i = 0;
    @FXML
    private Button btnPlot, btnConnect, btnTable, createBlueprintBtn, loadBlueprintBtn;
    @FXML
    private BorderPane mainBorderPane, borderPane;
    @FXML
    private ChoiceBox<Integer> stringLength;
    @FXML
    private CheckBox graphSelector;
    @FXML
    private CheckBox textSelector;
    @FXML
    private CheckBox hypercubeCheck;
    @FXML
    public CheckBox showTSPgraph;
    @FXML
    private Label searchspaceLabel,problemLabel, algorithmLabel,criteriasLabel,timeLabel,mutationLabel, selectionLabel,crossoverLabel;

    TSPParser tp;

    boolean animationDone = true;

    private Stage stage;
    private Scene scene;
    private Parent parent;
    private HashMap<String, String> blueprintChoices = new HashMap<>();
    private int bitStringValue;
    private boolean hypercubeSelected;
    @FXML
    private Button startButton,nextAlgorithm;
    private boolean isAnimationPaused = false;// Starts paused
   List<StoppingCriterion> stoppingCriteria;
   public int skipIterations;
   public boolean fullspeed = false;
    private ArrayList<Schedule> schedules;
    Schedule currentSchedule;

    public mainController() {

        animationTimer = new AnimationTimer() {
            private long lastUpdate = 0;
            @Override
            public void handle(long l) {
                if (l - lastUpdate >= duration) { // Update every second
                    if (!isAnimationPaused) {
                        sliderController();
                        lastUpdate = l;
                        double speed = sliderSpeed.getValue();
                        if(speed < (sliderSpeed.getMax()*0.5)) {
                            duration = (TimeUnit.MILLISECONDS.toNanos(1000) * (1 - speed / sliderSpeed.getMax()));
                            fullspeed = false;
                        } else{
                            duration = TimeUnit.MILLISECONDS.toNanos(0);
                            fullspeed = true;
                            skipIterations = (int) (speed - 500)/100;
                        }
                    }
                }
            }
        };
    }

    @FXML
    void createBlueprintHandler(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(main.class.getResource("fxml/createBlueprintPage.fxml")));
        Scene scene = new Scene(root);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Platform.runLater(root::requestFocus);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void loadBlueprintHandler(ActionEvent event) {
        fileChooser.showOpenDialog(stage);
    }

    @FXML
    void menuChangeHandler(ActionEvent event) throws IOException {
        if (event.getSource() == btnPlot) {
            changeContent("plotPage");
        } else if (event.getSource() == btnConnect) {
            changeContent("connectPage");
        } else if (event.getSource() == btnTable) {
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
    private ExecutorService executor = Executors.newFixedThreadPool(4); // Adjust based on needs

    public void executeSchedules(List<Schedule> schedules) {
        for (Schedule schedule : schedules) {
            executor.submit(() -> {
                schedule.setUpAlgorithm();
                schedule.run();
            });
        }
    }
    @FXML
    private void startAllEvolutions(Schedule schedule) {

        if (!isAnimationPaused) {
            isRunning = true;
            solutionArea.clear();
            container.getChildren().clear();
            hypercubenPane.getChildren().clear();
            tspVisualization.getChildren().clear();
            i=0;

            timesRun++;
            //currentSchedule.setUpAlgorithm();
            prepareUIBeforeAlgorithmRuns(schedule);


            Task<Void> runAlgorithmTask = new Task<>() {
                @Override
                protected Void call() {
                    schedule.run();  // This is where the algorithm runs on a background thread
                    return null;
                }
            };
            Algorithm algorithm = currentSchedule.getAlgorithm();


            runAlgorithmTask.setOnSucceeded(event -> Platform.runLater(() -> updateUIPostAlgorithm(currentSchedule)));
            runAlgorithmTask.setOnFailed(event -> Platform.runLater(() -> showError(runAlgorithmTask.getException())));

            new Thread(runAlgorithmTask).start();  // Start the task on a new thread

        } else {
            isAnimationPaused = false;
        }
    }





    public void prepareUIBeforeAlgorithmRuns(Schedule s) {
        // Apply UI changes that need to occur before the algorithm runs

        if(s.getTSP()){
            showTSPgraph.setVisible(true);
        } else {
            showTSPgraph.setVisible(false);
        }

        searchspaceLabel.setText(s.getSearchSpaceString());
        searchspaceLabel.setStyle("-fx-font-size: 10px;");
        problemLabel.setText(s.getProblemString());
        problemLabel.setStyle("-fx-font-size: 10px;");
        algorithmLabel.setText(s.getAlgorithmString());
        algorithmLabel.setStyle("-fx-font-size: 10px;");
        criteriasLabel.setText(s.getCriterias());
        criteriasLabel.setStyle("-fx-font-size: 10px;");

        if (graphSelector.isSelected()) {
            initializeChart();
            if (!flowPane.getChildren().contains(lineChart)) {
                flowPane.getChildren().add(lineChart);
            }
        }

        if (textSelector.isSelected()) {
            titleLabel = new Label("Run number: " + (timesRun));
            container.getChildren().addAll(titleLabel, solutionArea);
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            if (!flowPane.getChildren().contains(container)) {
                flowPane.getChildren().add(container);
            }
        }

        if (hypercubeCheck.isSelected()) {
            if (!flowPane.getChildren().contains(hypercubenPane)) {
                flowPane.getChildren().add(hypercubenPane);
            }
            booleanHypercubeVisualization = new BooleanHypercubeVisualization(s.getSearchSpace(), s.getProblem(), this, hypercubenPane);
        }

        if (showTSPgraph.isSelected()) {
            if (!flowPane.getChildren().contains(tspVisualization)) {
                tspVisualization.setPrefSize(600, 400);
                tspVisualization.setMinSize(600, 400);
                tspVisualization.setMaxSize(600, 400);
                tspVisualization.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
                flowPane.getChildren().add(tspVisualization);
            }
        }
    }
    int runNr;
    @FXML
    public void nextAlgorithm() {
        runNr++;
                System.out.println("Current schedule changed");
                if(runNr < queueSchedule.size()) {

                    currentSchedule = queueSchedule.get(runNr);
                    System.out.println(currentSchedule);
                    System.out.println(queueSchedule.toString());
                    startAllEvolutions(currentSchedule);
                } else {
                    nextAlgorithm.setDisable(true);
                    runNr = 0;
                    System.out.println("All schedules done");
                }
    }

    @FXML
    private void startEvolution() {
        startAllEvolutions(currentSchedule);
        if(queueSchedule.size() > 1) {
            nextAlgorithm.setDisable(false);
        }

       // executeSchedules(queueSchedule);
    }

    private void updateUIPostAlgorithm(Schedule schedule) {
        startAlgorithm();
        System.out.println("running");


        // Handle UI updates after algorithm completion
        // Possibly displaying results, stopping animations, etc.
    }

    public void sliderController() {
        Algorithm algorithm = currentSchedule.getAlgorithm();


        if(i < algorithm.finalList.size()) {
            Data data = algorithm.finalList.get(i);
            if(data.getImproved()) {
                if(fullspeed){
                    int skips = skipIterations;

                    if(i+skips < algorithm.finalList.size()) {
                        i += skipIterations;
                    }
                }
                runGraphics(algorithm,i);
                i++;
            } else {
                while (!data.getImproved() && i < algorithm.finalList.size()) {
                    data = algorithm.finalList.get(i);
                    i++;
                }
            }
        } else {
            System.out.println("stopped graphics");
            animationDone = true;
            System.out.println(runNr + " " + queueSchedule.size());
            if(runNr >= queueSchedule.size()-1) {
                System.out.println("All schedules done");
                nextAlgorithm.setDisable(true);
                runNr = 0;
            }
            stopGraphics();
        }

    }
    Circle lastCircle = null;
    public void runGraphics(Algorithm a, int i) {
        int generation = 0;
        String bitString = null;
        Data data = a.finalList.get(i);

        bitString = data.getBitString();
        generation = data.getGeneration();
        System.out.println("Generation: " + generation + "i er "+i);
        int fitness = data.getFitness();
        Optional<Double> temp = data.getTemp();
        double max = a.finalList.size()-1;
        generationSlider.setMax(max);
        generationSlider.setBlockIncrement(10);
        generationSlider.setMajorTickUnit(50);
        generationSlider.setSnapToTicks(true);
        generationSlider.adjustValue(i);

            if (isHypercubeSelected()) {
                Circle circle = booleanHypercubeVisualization.getDisplayCoordinates(bitString, false);
                if (circle != null) {
                    if (lastCircle != null) {
                        booleanHypercubeVisualization.hypercubePane.getChildren().remove(lastCircle);
                    }
                    if (i + 1 == a.finalList.size()) {
                        Circle perfectCircle = booleanHypercubeVisualization.getDisplayCoordinates(bitString, true);
                        booleanHypercubeVisualization.hypercubePane.getChildren().add(perfectCircle);
                    } else {
                        lastCircle = circle;
                        booleanHypercubeVisualization.hypercubePane.getChildren().add(circle);
                    }
                }
            }

            if (isTextSelected()) {
                if (i == 0) {
                    String initialText;
                    if (temp.isPresent()) {
                        initialText = ("Initial Solution: " + bitString + " with fitness: " + fitness + " temperature is " + temp + "\n");
                    } else {
                        initialText = ("Initial Solution: " + bitString + " with fitness: " + fitness + "\n");
                    }
                    solutionArea.appendText(initialText);
                } else {


                    String solutionText;
                    if (temp.isPresent()) {
                        solutionText = "Generation " + generation + ": New SA found: " + bitString + " with fitness: " + fitness + " temperature is " + temp + "\n";
                    } else {
                        solutionText = "Generation " + generation + ": New solution found: " + bitString + " with fitness: " + fitness + "\n";
                    }
                    solutionArea.appendText(solutionText);
                }
            }
            if (isGraphSelected()) {
                series.getData().add(new XYChart.Data<>(generation, fitness));
            }

            if (i == a.finalList.size() - 1) {
                String finalText = "Perfect solution found in generation " + generation + "\n";
                solutionArea.appendText(finalText);
            }
        }



    private void showError(Throwable th) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Error running algorithm: " + th.getMessage(), ButtonType.OK);
        alert.showAndWait();
    }
    int chartNr;
    public void initializeChart() {
        xAxis.setLabel("Generation");
        yAxis.setLabel("Fitness");
        lineChart.setTitle("Fitness Chart");
        lineChart.setAnimated(true);
        xAxis.setAnimated(true);
        yAxis.setAnimated(true);
        System.out.println("height + " + lineChart.getHeight() + "width " + lineChart.getWidth());
        series = new XYChart.Series<>();
        series.setName("Run number " + (chartNr + 1));
        lineChart.getData().add(series);
        chartNr++;
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

    @FXML
    private void hypercubeListener(ActionEvent event) {

        if (!hypercubeCheck.isSelected()) {
            hypercubeSelected = false;
            flowPane.getChildren().remove(hypercubenPane);
        } else {
            hypercubeSelected = true;
        }

    }

    public boolean isHypercubeSelected() {
        return hypercubeSelected;
    }
    ArrayList<Schedule> queueSchedule = new ArrayList<>();

    public void recieveArray(ArrayList<Schedule> schedules) {
        this.schedules = schedules;
        currentSchedule = schedules.get(0);
        Schedule s = currentSchedule;
        queueSchedule.clear();
        if(s.getTSP()){
            showTSPgraph.setVisible(true);
        } else {
            showTSPgraph.setVisible(false);
        }

        searchspaceLabel.setText(s.getSearchSpaceString());
        searchspaceLabel.setStyle("-fx-font-size: 10px;");
        problemLabel.setText(s.getProblemString());
        problemLabel.setStyle("-fx-font-size: 10px;");
        algorithmLabel.setText(s.getAlgorithmString());
        algorithmLabel.setStyle("-fx-font-size: 10px;");
        criteriasLabel.setText(s.getCriterias());
        criteriasLabel.setStyle("-fx-font-size: 10px;");
        for (int j = 0; j < schedules.size(); j++) {
            Schedule newSchedule = schedules.get(j);
            for (int k = 0; k < newSchedule.getRuns(); k++) {
            queueSchedule.add(newSchedule);
            }
        }

    }

    public void stopEvolution() {
        stopGraphics();
    }

    public void updateCanvas() {
        //algorithm.updateGraphics();
    }

    private void updateUIComponents() {
        // Logic to update other UI components like labels or tables
    }

    public void startAlgorithm() {
        // Initialize your algorithm runner here
        animationTimer.start(); // Start the animation
    }

    @FXML
    private void pauseGraphics() {
        isAnimationPaused = true;
    }

    @FXML
    private void continueSlider() {
        int newI = (int) generationSlider.getValue();
        currentSchedule.getAlgorithm().clearAndContinue(i, newI);
        isAnimationPaused = false;
    }


    public void stopGraphics() {
        //wait 5 sec

        animationTimer.stop(); // Stop the animation
        isRunning = false; // Set running state to false to stop the algorithm
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Schedule schedule = new Schedule();
        schedule.setRuns(1);
        schedule.setOptimumReached(true);
        schedule.setSearchSpaceString("Bit strings");
        schedule.setProblemString("OneMax");
        schedule.setAlgorithmString("RLS");
        schedule.setDimension(100);

        Schedule schedule2 = new Schedule();
        schedule2.setRuns(1);
        schedule2.setOptimumReached(true);
        schedule2.setSearchSpaceString("Bit strings");
        schedule2.setProblemString("OneMax");
        schedule2.setAlgorithmString("(1+1) EA");
        schedule2.setDimension(100);

        Schedule schedule3 = new Schedule();
        schedule3.setRuns(1);
        schedule3.setOptimumReached(true);
        schedule3.setSearchSpaceString("Bit strings");
        schedule3.setProblemString("LeadingOnes");
        schedule3.setAlgorithmString("RLS");
        schedule3.setDimension(100);



        queueSchedule.add(schedule);
        queueSchedule.add(schedule2);
        queueSchedule.add(schedule3);
        currentSchedule = schedule;
        nextAlgorithm.setDisable(true);
        showTSPgraph.setVisible(false);
        //recieveArray(queueSchedule);
        //prepareUIBeforeAlgorithmRuns(new BitString(5000), new OneMax(new BitString(5000)));


    }

    public boolean isTextSelected() {
        return textSelector.isSelected();
    }

    public boolean isGraphSelected() {
        return graphSelector.isSelected();
    }
}