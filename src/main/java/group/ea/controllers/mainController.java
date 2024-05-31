package group.ea.controllers;


import group.ea.main;
import group.ea.structure.TSP.Solution;
import group.ea.structure.TSP.TSPParser;
import group.ea.structure.algorithm.*;
import group.ea.structure.algorithm.BooleanHypercubeVisualization;
import group.ea.structure.algorithm.Algorithm;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class mainController implements Initializable, AlgorithmUpdateListener {
    private static AnimationTimer animationTimer;
    public NumberAxis xAxis = new NumberAxis();
    public NumberAxis yAxis = new NumberAxis();
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
    private GridPane batchInfo;
    @FXML
    private GridPane statsBatch;
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
    private CheckBox graphSelector,graphicsToggle;
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
    int maxIterationsLabel = 0;
    private Label batchNumberLabel = new Label();
    private Label timesRunLabel = new Label();
    private Label dimensionLabel = new Label();
    private Label maxIterationsLabels = new Label();
    private Label averageIterationsLabel = new Label();

    private boolean firstCall = true;
    private final Map<Edge, Line> edgeMap = new HashMap<>();
    ArrayList<TSPDATA> allSolutions;
    double speed;
    int maxY;
    /*@FXML
    private Pane tspVisualization;



    @FXML
    private Button startButton;
    @FXML
     */
    @FXML
    private Slider speedSlider;
    @FXML
    private Button pauseButton;
    @FXML
    private Button stopButton;
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

    public mainController() {

        animationTimer = new AnimationTimer() {
            private long lastUpdate = 0;
            @Override
            public void handle(long l) {
                System.out.println("running");
                if (l - lastUpdate >= duration) { // Update every second
                   if (!isAnimationPaused) {
                       System.out.println("running");
                        sliderController();
                        lastUpdate = l;
                        double speed = speedSlider.getValue();
                       System.out.println("Speed: " + speed);
                        if(speed < (speedSlider.getMax()*0.5)) {
                            duration = (TimeUnit.MILLISECONDS.toNanos(1000) * (1 - speed / sliderSpeed.getMax()));
                            fullspeed = false;
                        } else{
                            duration = TimeUnit.MILLISECONDS.toNanos(0);
                            fullspeed = true;
                            skipIterations = (int) (speed - 500)/10;
                       }
                    }
                }
            }
        };
    }

    @FXML
    void createBlueprintHandler(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(main.class.getResource("fxml/createSchedulePage.fxml")));
        Scene scene = new Scene(root);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Platform.runLater(root::requestFocus);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void loadScheduleHandler(ActionEvent event) {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        Schedule newSchedule = new Schedule();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                Map<String, String[]> dataMap = new HashMap<>();

                while ((line = br.readLine()) != null) {
                    // Split the line by commas, and remove any extra whitespace
                    String[] parts = line.split(",\\s*");
                    if (parts.length > 1) {
                        String key = parts[0].trim();
                        String[] values = new String[parts.length - 1];
                        System.arraycopy(parts, 1, values, 0, parts.length - 1);
                        dataMap.put(key, values);
                    }
                }

                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String[]> entry : dataMap.entrySet()) {
                    sb.append(entry.getKey()).append("=").append(Arrays.toString(entry.getValue())).append(", ");
                }
                System.out.println( sb.toString());

                newSchedule.setSearchSpaceString(dataMap.get("Searchspace")[0]);
                newSchedule.setProblemString(dataMap.get("Problem")[0]);
                newSchedule.setAlgorithmString(dataMap.get("Algorithm")[0]);



                if (dataMap.containsKey("Dimension")){
                    newSchedule.setDimension(Integer.parseInt(dataMap.get("Dimension")[0]));
                }

                if (dataMap.containsKey("Stopping criterias")) {
                    while (dataMap.get("Stopping criterias").length > 0){
                        String[] stoppingCriterias = dataMap.get("Stopping criterias");
                        String readCrit = stoppingCriterias[0];
                        String readVal = "";
                        if (!(dataMap.get("Stopping criterias").length == 1)){
                            readVal = stoppingCriterias[1];
                        }

                        if (readCrit.equals("Iteration bound")){
                            newSchedule.setIterationBound(Integer.parseInt(readVal));
                        }
                        else if (readCrit.equals("Fitness bound")){
                            newSchedule.setFitnessBound(Integer.parseInt(readVal));
                        }
                        else if (readCrit.equals("Optimum reached")){
                            newSchedule.setOptimumReached(true);
                        }
                        dataMap.put("Stopping criterias", removeElementFromArray(dataMap.get("Stopping criterias"), readCrit));
                        dataMap.put("Stopping criterias", removeElementFromArray(dataMap.get("Stopping criterias"), readVal));
                    }

                }

                if (dataMap.containsKey("Special parameters")){
                    String[] special_parameters = dataMap.get("Special parameters");
                    if (dataMap.get("Algorithm")[0].equals("Ant System")){
                        String colonySize = special_parameters[0];
                        String alpha = special_parameters[1];
                        String beta = special_parameters[2];
                        String[] optionalValues = new String[]{colonySize, alpha, beta};

                        newSchedule.setOptional(optionalValues);
                    }
                    else if (dataMap.get("Algorithm")[0].equals("(u+y) EA")){
                        String mu = special_parameters[0];
                        String lambda = special_parameters[1];
                        String[] optionalValues = new String[]{mu, lambda};

                        //newSchedule.setOptional(optionalValues);
                        newSchedule.setMu(Integer.parseInt(mu));
                        newSchedule.setLambda(Integer.parseInt(lambda));
                    }
                }

                if (dataMap.get("Problem")[0].equals("TSP")){
                    newSchedule.setTSPProblem(dataMap.get("TSP problem")[0]);
                }

                newSchedule.setUpAlgorithm();

                recieveArray(Schedule.getSchedules());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }

    private static String[] removeElementFromArray(String[] array, String element) {
        if (array == null || array.length == 0) {
            return array;
        }

        int count = 0;
        for (String item : array) {
            if (!item.equals(element)) {
                count++;
            }
        }

        if (count == array.length) {
            return array;  // Element not found, return original array
        }

        String[] newArray = new String[count];
        int index = 0;
        for (String item : array) {
            if (!item.equals(element)) {
                newArray[index++] = item;
            }
        }

        return newArray;
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
        if (currentSchedule.getProblemString() == "TSP") {
            sliderSpeed.setVisible(true);
            speedSlider.setVisible(false);
            resetVisualization();
            startVisualization();

        } else {
            sliderSpeed.setVisible(false);
            speedSlider.setVisible(true);

            if (!isAnimationPaused) {
                isRunning = true;
                solutionArea.clear();
                container.getChildren().clear();
                hypercubenPane.getChildren().clear();
                tspVisualization.getChildren().clear();
                i = 0;

                timesRun++;
                //currentSchedule.setUpAlgorithm();
                prepareUIBeforeAlgorithmRuns(schedule);
                schedule.getAlgorithm().sendListener(this);

                Task<Void> runAlgorithmTask = new Task<>() {
                    @Override
                    protected Void call() {
                        schedule.run();  // This is where the algorithm runs on a background thread
                        return null;
                    }
                };
                Algorithm algorithm = currentSchedule.getAlgorithm();

                runAlgorithmTask.setOnSucceeded(event -> Platform.runLater(() -> updateStatistics(currentSchedule)));
                runAlgorithmTask.setOnSucceeded(event -> Platform.runLater(() -> updateUIPostAlgorithm(currentSchedule)));
                runAlgorithmTask.setOnFailed(event -> Platform.runLater(() -> showError(runAlgorithmTask.getException())));

                new Thread(runAlgorithmTask).start();  // Start the task on a new thread

            } else {
                isAnimationPaused = false;
            }
        }
    }
    private void updateStatistics(Schedule schedule){
        schedule.setFinishedIterations(schedule.getAlgorithm().getGeneration());

        // Update statistics here
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
        if(!graphicsToggle.isSelected()) {
            startAlgorithm();
            System.out.println("running");
        } else {
            updateUIStats();
        }

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
                while (!data.getImproved() && i < algorithm.finalList.size()-1) {
                    i++;
                    data = algorithm.finalList.get(i);
                }
            }
        } else {
            updateUIStats();
            System.out.println("stopped graphics");
            System.out.println(i + "i er " + algorithm.finalList.size());
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
    int sum = 0;
    private void updateUIStats() {

        timesRunLabel.setText(""+timesRun);
        dimensionLabel.setText(""+currentSchedule.getDimension());


        int iter = currentSchedule.getAlgorithm().getGeneration();
        sum += iter;
        if(iter > maxIterationsLabel) {
            maxIterationsLabel = iter;
            maxIterationsLabels.setText(""+maxIterationsLabel);
        }


        averageIterationsLabel.setText(""+sum/timesRun);


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
           // newSchedule.getAlgorithm().sendListener(this);
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
       // isAnimationPaused = true;
       // pauseVisualization();
    }

    @FXML
    private void continueSlider() {
        int newI = (int) generationSlider.getValue();
        currentSchedule.getAlgorithm().clearAndContinue(i, newI);
        isAnimationPaused = false;
    }


    public void stopGraphics() {
        //wait 5 sec
        resetVisualization();

        animationTimer.stop(); // Stop the animation
        isRunning = false; // Set running state to false to stop the algorithm
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        batchInfo.add(batchNumberLabel, 1, 0);
        batchInfo.add(timesRunLabel, 1, 1);
        batchInfo.add(dimensionLabel, 1, 2);
        statsBatch.add(maxIterationsLabels, 1, 0);
        statsBatch.add(averageIterationsLabel, 1, 1);
        batchInfo.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14;");
        statsBatch.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14;");
        batchNumberLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14;");
        timesRunLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14;");
        dimensionLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14;");
        maxIterationsLabels.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14;");
        averageIterationsLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14;");


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
    @FXML
    private ScatterChart<Number, Number> scatterChart;
    @FXML
    private Pane overlayPane;
    @FXML
    private ScrollPane scrollPaneMain;

    public void tspIntialize(){


        fitnessLabel = new Label("Fitness: ");
        numberOfEdgesLabel = new Label("Time elapsed: 0ms");
        gainLabel = new Label("Gain: ");
        edgesDeletedLabel = new Label("Edges Deleted: 0");
        edgesAddedLabel = new Label("Edges Added: 0");

        VBox infoBox = new VBox(fitnessLabel, numberOfEdgesLabel, gainLabel, edgesDeletedLabel, edgesAddedLabel);
        infoBox.setSpacing(5);
        flowPane.getChildren().add(infoBox);



        sliderSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (timeline != null) {
                timeline.stop(); // Stop the timeline to reset the key frame duration
                double speed = newValue.doubleValue();
                KeyFrame keyFrame = new KeyFrame(Duration.seconds(1 / speed), event -> {
                    System.out.println("Keyframe 1 running");
                    processQueue();
                });
                timeline.getKeyFrames().setAll(keyFrame); // Set the new key frame with the updated speed
                timeline.play(); // Restart the timeline with the new speed
            }
        });

    }

    private final Queue<TSPDATA> updateQueue = new LinkedList<>();

    public void setSolution(TSPDATA initialSolution) {
       /* if(firstCall){
            firstSolution(initialSolution.solution);
            firstCall = false;
        }*/
        this.currentSolution = initialSolution;
        this.nextSolution = initialSolution;// Copy initial solution
       // this.changedEdges = new HashSet<>();
        //resetVisualization();
    }
    @FXML
    private void startVisualization() {

        sliderSpeed.setBlockIncrement(1.0);
        sliderSpeed.setMax(10.0);
        sliderSpeed.setMin(0.1);
        sliderSpeed.setValue(1.0);
        sliderSpeed.setMajorTickUnit(2.0);
        currentSchedule.getAlgorithm().sendListener(this);
        currentSchedule.run();
        if (isPaused) {
            timeline.play();
            isPaused = false;
            pauseButton.setDisable(false);
            startButton.setDisable(true);
            return;
        }

        //resetVisualization();
        tspIntialize();
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        speed = sliderSpeed.getValue();


        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1 / speed), event -> {
            processQueue();
            System.out.println("Keyframe 2 running");

        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();

        startButton.setDisable(true);
        pauseButton.setDisable(false);
        stopButton.setDisable(false);
    }

    @FXML
    private void pauseVisualization() {
        if (timeline != null) {
            timeline.pause();
           // isPaused = true;
            startButton.setDisable(false);
            pauseButton.setDisable(true);
        }
    }

    @FXML
    private void resetVisualization() {
        if (timeline != null) {
            timeline.stop();
        }
        stackPane.getChildren().clear();
        currentStep = 0;

        startButton.setDisable(false);
        pauseButton.setDisable(true);
        stopButton.setDisable(true);
        isPaused = false;


    }

    double xScaling = 3.4;
    double yScaling = 3.4;
    double xPush = 35;

    StackPane stackPane = new StackPane();
    @Override
    public void firstSolution(Solution solution) {


        System.out.println( "First solution");
        Solution firstSolution = solution;
        int numPoints = firstSolution.getListLength();
        double maxXFirst = 0;
        double maxYFirst = 0;

        for (int i = 0; i < numPoints; i++) {
            int x = firstSolution.getXSolution(i);
            int y = firstSolution.getYSolution(i);
            if (x > maxXFirst) {
                maxXFirst = x;
            }
            if (y > maxYFirst) {
                maxYFirst = y;
            }

        }
        xAxis = new NumberAxis(0, maxXFirst, maxXFirst/10);
        yAxis = new NumberAxis(0, maxYFirst, maxYFirst/10);
        yAxis.setTickLabelRotation(90);

        scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setPrefSize(600, 400);
        scatterChart.setMinSize(600, 400);
        scatterChart.setMaxSize(600, 400);
        scatterChart.setLegendVisible(false);



        stackPane.getChildren().addAll(scatterChart, tspVisualization);
        flowPane.getChildren().add(stackPane);

        double yPush = 40 / (1200 / maxXFirst);
        maxY = (int) ((int) maxYFirst + yPush);
        System.out.println(maxXFirst + " " + maxYFirst);
        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        Circle circle4 = new Circle(40,360, 3, Color.RED);
        tspVisualization.getChildren().add(circle4);
        Circle circle1 = new Circle(40,18, 3, Color.RED);
        tspVisualization.getChildren().add(circle1);
        Circle circle2 = new Circle(590,360, 3, Color.RED);
        tspVisualization.getChildren().add(circle2);
        Circle circle3 = new Circle(590,18, 3, Color.RED);
        tspVisualization.getChildren().add(circle3);

        double graphWidth = 590 - 40;
        double graphHeight = 360 - 18;
        double extraScaleX = 1800 / maxXFirst;
        double extraScaleY = 1200 / maxYFirst;
        xScaling = (1800 / graphWidth) / extraScaleX;
        yScaling = 1200 / graphHeight / extraScaleY;


        // Draw the nodes
        for (int i = 0; i < numPoints; i++) {
            int x = firstSolution.getXSolution(i);
            int y = maxY - firstSolution.getYSolution(i);
            Circle circle = new Circle(xPush+x / xScaling, y / yScaling, 3, Color.RED);
            tspVisualization.getChildren().add(circle);
            //series.getData().add(new XYChart.Data<>(x, y));
        }
        System.out.println(series.toString());
        //scatterChart.getData().add(series);
        // Draw the edges
        for (int i = 0; i < numPoints; i++) {
            int x1 = firstSolution.getXSolution(i);
            int y1 = maxY - firstSolution.getYSolution(i);
            int x2 = firstSolution.getXSolution((i + 1) % numPoints);
            int y2 = maxY - firstSolution.getYSolution((i + 1) % numPoints);
            Line line = new Line(xPush+x1 / xScaling, y1 / yScaling, xPush+x2 / xScaling, y2 / yScaling);
            edgeMap.put(new Edge(x1, y1, x2, y2), line);
            tspVisualization.getChildren().add(line);
            //System.out.println("Adding line "+ new Edge(x1, y1, x2, y2));
            //overlayPane.getChildren().add(line);
        }
        //printEdgeMapDetails();

    }

    // Method to delete all existing edges
    private void deleteAllEdges() {
        for (Line line : edgeMap.values()) {
            tspVisualization.getChildren().remove(line);
        }
        edgeMap.clear();
    }

    // Modified deleteAndDraw method
    public void deleteAndDraw(Solution solution) {
        // Delete all existing edges
        deleteAllEdges();

        // Draw the new edges from the solution
        for (int i = 0; i < solution.getDimension(); i++) {
            int x1 = solution.getXSolution(i);
            int y1 = maxY - solution.getYSolution(i);
            int x2 = solution.getXSolution((i + 1) % solution.getDimension());
            int y2 = maxY - solution.getYSolution((i + 1) % solution.getDimension());
            Line line = new Line(xPush + x1 / xScaling, y1 / yScaling, xPush + x2 / xScaling, y2 / yScaling);
            edgeMap.put(new Edge(x1, y1, x2, y2), line);
            tspVisualization.getChildren().add(line);
            //System.out.println("Adding line " + new Edge(x1, y1, x2, y2));
        }
        updateLabels();
    }







    private void updateVisualization() {

        // Clear previous visualization
        speed = sliderSpeed.getValue();
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
                System.out.println("Line6 not found for edge: " +new Edge(x5, y5, x6, y6));
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
            Line newLine1 = new Line(xPush + x1 / xScaling, y1 / yScaling, xPush + x3 / xScaling, y3 / yScaling);
            Line newLine2 = new Line(xPush + x2 / xScaling, y2 / yScaling, xPush + x4 / xScaling, y4 / yScaling);
            edgesAdded++;
            edgesAdded++;

            newLine1.setStroke(Color.GREEN);
            newLine2.setStroke(Color.GREEN);
            tspVisualization.getChildren().add(newLine1);
            tspVisualization.getChildren().add(newLine2);
            System.out.println("Adding 2 line "+ new Edge(x1, y1, x3, y3));
            System.out.println("Adding 2 line "+ new Edge(x2, y2, x4, y4));
            edgeMap.put(new Edge(x1, y1, x3, y3), newLine1);
            edgeMap.put(new Edge(x2, y2, x4, y4), newLine2);
        }
        for (Edge edge : newEdges) {
            Line newLine = new Line(xPush + edge.x1 / xScaling, edge.y1 / yScaling, xPush +edge.x2 / xScaling, edge.y2 / yScaling);
            newLine.setStroke(Color.GREEN);
            System.out.println("Adding 3 lines");
            System.out.println("Adding 3 lines "+ edge);
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
        numberOfEdgesLabel.setText("Time elapsed: " + currentSolution.getTimeElapsed() + "ms");
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

    boolean ACO = true;

    private void processQueue() {
        if (!updateQueue.isEmpty()) {
            TSPDATA nextSolution = updateQueue.poll();
            setSolution(nextSolution);
            if(ACO){
                deleteAndDraw(nextSolution.getSolution());
            }else{
                updateVisualization();
            }


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





