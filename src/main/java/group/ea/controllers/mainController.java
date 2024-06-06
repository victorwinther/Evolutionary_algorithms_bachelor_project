package group.ea.controllers;


import group.ea.main;
import group.ea.StoppingCriterias.StoppingCriterion;
import group.ea.problem.TSP.Solution;
import group.ea.problem.TSP.TSPParser;
import group.ea.algorithm.*;
import group.ea.algorithm.BooleanHypercubeVisualization;
import group.ea.algorithm.Algorithm;
import group.ea.helperClasses.Data;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.cell.PropertyValueFactory;
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
    private CheckBox graphSelector, textSelector, hypercubeCheck;

    @FXML
    private Label searchspaceLabel,problemLabel, algorithmLabel,criteriasLabel,timeLabel,mutationLabel, selectionLabel,crossoverLabel;

    Label minIterationsLabel = new Label();
    Label maxFuncEvalLabel = new Label();
    Label minFuncEvalLabel = new Label();
    Label maxFitnessLabel = new Label();
    Label averageFitnessLabel = new Label();
    Label minFitnessLabel = new Label();
    TSPParser tp;

    boolean animationDone = true;

    private Stage stage;
    private Scene scene;
    private Parent parent;
    private HashMap<String, String> blueprintChoices = new HashMap<>();
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

    @FXML
    private TableView<RowData> extractKeyFeaturesTable;
    @FXML
    private TableColumn<RowData,String> tableIterations, tableFuncEval,tableFitness,tableOptimalFitness,tableRuntime;
    /*@FXML
    private Pane tspVisualization;



    @FXML
    private Button startButton;
    @FXML
     */

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

    }

    @FXML
    void createBlueprintHandler(ActionEvent event) throws IOException {
        if(timeline != null) {
            timeline.stop();
        }
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
        Map<String, String> scheduleParameters = new HashMap<>();
        addParametersToMap(scheduleParameters);

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                Map<String, String[]> dataMap = new HashMap<>();
                Map<String, Map<String, String>> batchMap = new HashMap<>();
                boolean isBatch = false;

                while ((line = br.readLine()) != null) {
                    // Split the line by commas, and remove any extra whitespace
                    String[] parts = line.split(",\\s*");

                    if (line.trim().equalsIgnoreCase("batch")) {
                        isBatch = true;
                        continue;
                    }

                    if (isBatch) {
                        // Read batch lines into batchMap
                        String[] keyValuePairs = line.split(";\\s*");
                        Map<String, String> attributes = new HashMap<>();
                        String id = null;

                        for (String kv : keyValuePairs) {
                            String[] kvParts = kv.split(",\\s*");
                            if (kvParts.length > 1) {
                                String key = kvParts[0].trim();
                                String value = kvParts[1].trim();
                                if (key.equals("id")) {
                                    id = value;
                                } else {
                                    attributes.put(key, value);
                                }
                            }
                        }

                        if (id != null) {
                            batchMap.put(id, attributes);
                        }
                    } else {
                        // Read regular lines into dataMap
                        if (parts.length > 1) {
                            String key = parts[0].trim();
                            String[] values = new String[parts.length - 1];
                            System.arraycopy(parts, 1, values, 0, parts.length - 1);
                            dataMap.put(key, values);
                        }
                    }
                }

                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String[]> entry : dataMap.entrySet()) {
                    sb.append(entry.getKey()).append("=").append(Arrays.toString(entry.getValue())).append(", ");
                }
                System.out.println( sb.toString());
                System.out.println("Batch entries:");
                for (Map.Entry<String, Map<String, String>> entry : batchMap.entrySet()) {
                    System.out.println("ID: " + entry.getKey());
                    for (Map.Entry<String, String> attribute : entry.getValue().entrySet()) {
                        System.out.println("  " + attribute.getKey() + ": " + attribute.getValue());
                    }
                }

                scheduleParameters.put("searchspace", dataMap.get("Searchspace")[0]);
                scheduleParameters.put("problem", dataMap.get("Problem")[0]);
                scheduleParameters.put("algorithm", dataMap.get("Algorithm")[0]);


                if (dataMap.containsKey("Dimension")){
                    scheduleParameters.put("dimension", dataMap.get("Dimension")[0]);
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
                            scheduleParameters.put("iterationbound", readVal);
                        }
                        else if (readCrit.equals("Fitness bound")){
                            scheduleParameters.put("fitnessbound", readVal);
                        }
                        else if (readCrit.equals("Optimum reached")){
                            scheduleParameters.put("optimalbound", "true");
                        }
                        dataMap.put("Stopping criterias", removeElementFromArray(dataMap.get("Stopping criterias"), readCrit));
                        dataMap.put("Stopping criterias", removeElementFromArray(dataMap.get("Stopping criterias"), readVal));
                    }

                }

                if (dataMap.containsKey("Special parameters")){
                    String[] special_parameters = dataMap.get("Special parameters");
                    if (dataMap.get("Algorithm")[0].equals("Ant Colony Optimization")){
                        String colonySize = special_parameters[0];
                        String alpha = special_parameters[1];
                        String beta = special_parameters[2];
                        String updateRule = dataMap.get("ACO update rule")[0];
                        String localSearch = dataMap.get("local search")[0];

                        scheduleParameters.put("colonysize", colonySize);
                        scheduleParameters.put("alpha", alpha);
                        scheduleParameters.put("beta", beta);
                        scheduleParameters.put("updaterule", updateRule);
                        scheduleParameters.put("localsearch", localSearch);
                    }
                    else if (dataMap.get("Algorithm")[0].equals("(u+y) EA")){
                        String mu = special_parameters[0];
                        String lambda = special_parameters[1];

                        scheduleParameters.put("mu", mu);
                        scheduleParameters.put("lambda", lambda);
                    }
                }

                if (dataMap.get("Problem")[0].equals("TSP")){
                    String tspProblem = dataMap.get("TSP problem")[0];

                    scheduleParameters.put("tspproblem", tspProblem);
                }

                if (isBatch){
                    for (String scheduleid : batchMap.keySet()){
                        Schedule batchSchedule = new Schedule();
                        batchSchedule.setSearchSpaceString(scheduleParameters.get("searchspace"));
                        batchSchedule.setProblemString(scheduleParameters.get("problem"));
                        batchSchedule.setAlgorithmString(scheduleParameters.get("algorithm"));

                        if (batchMap.get(scheduleid).containsKey("Dimension")){
                            batchSchedule.setDimension(Integer.parseInt(batchMap.get(scheduleid).get("Dimension")));
                        }
                        if (batchMap.get(scheduleid).containsKey("I. Iterations")){
                            batchSchedule.setIterationBound(Integer.parseInt(batchMap.get(scheduleid).get("I. Iterations")));
                        }
                        if (batchMap.get(scheduleid).containsKey("F. Iterations")){
                            batchSchedule.setFitnessBound(Integer.parseInt(batchMap.get(scheduleid).get("F. Iterations")));
                        }
                        if (batchMap.get(scheduleid).containsKey("Optimal")){
                            batchSchedule.setOptimumReached(true);
                        }
                        if (batchMap.get(scheduleid).containsKey("TSP problem")){
                            batchSchedule.setTSPProblem(batchMap.get(scheduleid).get("TSP problem"));
                        }

                        if (scheduleParameters.get("algorithm").equals("Ant Colony Optimization")){
                            String colonySize = scheduleParameters.get("colonysize");
                            String alpha = scheduleParameters.get("alpha");
                            String beta = scheduleParameters.get("beta");
                            String updateRule = scheduleParameters.get("updaterule");
                            String localSearch = scheduleParameters.get("localsearch");
                            String[] optionalValues = new String[]{colonySize, alpha, beta};

                            batchSchedule.setLocalSearch(localSearch.equals("true"));
                            batchSchedule.setUpdateRule(updateRule);
                            batchSchedule.setOptional(optionalValues);
                        }
                        if (scheduleParameters.get("algorithm").equals("(u+y) EA")){
                            batchSchedule.setMu(Integer.parseInt(batchMap.get(scheduleid).get("u")));
                            batchSchedule.setLambda(Integer.parseInt(batchMap.get(scheduleid).get("y")));
                        }

                        batchSchedule.setUpAlgorithm();
                    }


                } else {
                    Schedule newSchedule = new Schedule();
                    newSchedule.setSearchSpaceString(scheduleParameters.get("searchspace"));
                    newSchedule.setProblemString(scheduleParameters.get("problem"));
                    newSchedule.setAlgorithmString(scheduleParameters.get("algorithm"));

                    if (!scheduleParameters.get("dimension").isEmpty()){
                        newSchedule.setDimension(Integer.parseInt(scheduleParameters.get("dimension")));
                    }
                    if (!scheduleParameters.get("iterationbound").isEmpty()){
                        newSchedule.setIterationBound(Integer.parseInt(scheduleParameters.get("iterationbound")));
                    }
                    if (!scheduleParameters.get("fitnessbound").isEmpty()){
                        newSchedule.setFitnessBound(Integer.parseInt(scheduleParameters.get("fitnessbound")));
                    }
                    if (!scheduleParameters.get("optimalbound").isEmpty()){
                        newSchedule.setOptimumReached(true);
                    }
                    if (!scheduleParameters.get("tspproblem").isEmpty()){
                        newSchedule.setTSPProblem(scheduleParameters.get("tspproblem"));
                    }
                    if (scheduleParameters.get("algorithm").equals("Ant Colony Optimization")){
                        String colonySize = scheduleParameters.get("colonysize");
                        String alpha = scheduleParameters.get("alpha");
                        String beta = scheduleParameters.get("beta");
                        String updateRule = scheduleParameters.get("updaterule");
                        String localSearch = scheduleParameters.get("localsearch");
                        String[] optionalValues = new String[]{colonySize, alpha, beta};

                        newSchedule.setLocalSearch(localSearch.equals("true"));
                        newSchedule.setUpdateRule(updateRule);
                        newSchedule.setOptional(optionalValues);
                    }
                    if (scheduleParameters.get("algorithm").equals("(u+y) EA")){
                        newSchedule.setMu(Integer.parseInt(scheduleParameters.get("mu")));
                        newSchedule.setLambda(Integer.parseInt(scheduleParameters.get("lambda")));
                    }

                    newSchedule.setUpAlgorithm();
                }

                recieveArray(Schedule.getSchedules());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }

    private void addParametersToMap(Map<String, String> map){
        map.put("searchspace", "");
        map.put("problem", "");
        map.put("algorithm", "");
        map.put("dimension", "");
        map.put("iterationbound", "");
        map.put("fitnessbound", "");
        map.put("optimalbound", "");
        map.put("colonysize", "");
        map.put("tspproblem", "");
        map.put("updaterule", "");
        map.put("localsearch", "");
        map.put("alpha", "");
        map.put("beta", "");
        map.put("mu", "");
        map.put("lambda", "");
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
        prepareUIBeforeAlgorithmRuns(schedule);
        if (currentSchedule.getProblemString().equals("TSP")) {
            resetVisualization();
            startVisualization();

        } else {
            startVisualizationBitString();
        }
        timesRun++;
        updateStatistics(currentSchedule);


    }
    private void updateStatistics(Schedule schedule){
        schedule.setFinishedIterations(schedule.getAlgorithm().getGeneration());

    }

    public void prepareUIBeforeAlgorithmRuns(Schedule s) {


        searchspaceLabel.setText(s.getSearchSpaceString());
        searchspaceLabel.setStyle("-fx-font-size: 13px;");
        problemLabel.setText(s.getProblemString());
        problemLabel.setStyle("-fx-font-size: 13px;");
        algorithmLabel.setText(s.getAlgorithmString());
        algorithmLabel.setStyle("-fx-font-size: 13px;");
        criteriasLabel.setText(s.getCriterias());
        criteriasLabel.setStyle("-fx-font-size: 13px;");

        if (graphSelector.isSelected()) {
            initializeChart();
            if (!flowPane.getChildren().contains(lineChart)) {
                flowPane.getChildren().add(lineChart);
            }
        }

        if (textSelector.isSelected()) {
            titleLabel = new Label("Run number: " + (timesRun));
            container.getChildren().clear();
            solutionArea.clear();
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
            booleanHypercubeVisualization = new BooleanHypercubeVisualization(s.getSearchSpace(), s.getProblem(), this, hypercubenPane,runNr);
        }


    }
    int runNr;
    @FXML
    public void nextAlgorithm() {
                firstTime = true;
                System.out.println("Current schedule changed");
                if(runNr < queueSchedule.size()) {
                    currentSchedule = queueSchedule.get(runNr);
                    System.out.println(currentSchedule);
                    System.out.println(queueSchedule.toString());
                    if(currentSchedule.getTSP()){
                        graphSelector.setDisable(true);
                        textSelector.setDisable(true);
                        hypercubeCheck.setDisable(true);

                    }
                    startAllEvolutions(currentSchedule);
                    runNr++;
                    startButton.setDisable(true);
                } else {
                    nextAlgorithm.setDisable(true);
                    runNr = 0;
                    System.out.println("All schedules done");
                }
    }

    @FXML
    private void startEvolution() {
        graphSelector.setDisable(!graphSelector.isSelected());
        textSelector.setDisable(!textSelector.isSelected());
        hypercubeCheck.setDisable(!hypercubeCheck.isSelected());

        if (isPaused) {
            timeline.play();
            currentSchedule.getAlgorithm().resume();
            isPaused = false;
            pauseButton.setDisable(false);
            startButton.setDisable(true);
            return;
        }
        System.out.println("Starting evolution, queue size: " + queueSchedule.size());
        if(queueSchedule.size() > 1) {
            nextAlgorithm.setDisable(false);
        }
        nextAlgorithm();


       // executeSchedules(queueSchedule);
    }

    private void updateUIPostAlgorithm(Schedule schedule) {
            updateUIStats();
    }

    int sum = 0;
    int minIt = 1000000000;
    int maxFunc = 0;
    int minFunc = 1000000000;
    int maxFit = 0;
    int avgFit = 0;
    int minFit = 1000000000;
    private void updateUIStats() {

        batchNumberLabel.setText(String.valueOf(schedules.size()));

        timesRunLabel.setText(""+timesRun);
        dimensionLabel.setText(""+currentSchedule.getDimension());


        int iter = currentSchedule.getAlgorithm().getGeneration();
        sum += iter;
        if(iter > maxIterationsLabel) {
            maxIterationsLabel = iter;
            maxIterationsLabels.setText(""+maxIterationsLabel);
        }


        averageIterationsLabel.setText(""+sum/timesRun);
        if(iter < minIt) {
            minIt = iter;
            minIterationsLabel.setText(""+minIt);
        }
        if(maxFunc < currentSchedule.getAlgorithm().getFunctionEvaluations()) {
            maxFunc = currentSchedule.getAlgorithm().getFunctionEvaluations();
            maxFuncEvalLabel.setText(""+maxFunc);
        }
        if(minFunc > currentSchedule.getAlgorithm().getFunctionEvaluations()) {
            minFunc = currentSchedule.getAlgorithm().getFunctionEvaluations();
            minFuncEvalLabel.setText(""+minFunc);
        }
        if(maxFit < currentSchedule.getAlgorithm().getFitness()) {
            maxFit = currentSchedule.getAlgorithm().getFitness();
            maxFitnessLabel.setText(""+maxFit);
        }
        avgFit += currentSchedule.getAlgorithm().getFitness();
        averageFitnessLabel.setText(""+avgFit/timesRun);
        if(minFit > currentSchedule.getAlgorithm().getFitness()) {
            minFit = currentSchedule.getAlgorithm().getFitness();
            minFitnessLabel.setText(""+minFit);
        }



    }
    Circle lastCircle = null;


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
        if(!s.getTSP()){
            graphSelector.setDisable(false);
            textSelector.setDisable(false);
            hypercubeCheck.setDisable(false);

        }
        startButton.setDisable(false);
        sliderSpeed.setDisable(false);


        searchspaceLabel.setText(s.getSearchSpaceString());
        searchspaceLabel.setStyle("-fx-font-size: 14px;");
        problemLabel.setText(s.getProblemString());
        problemLabel.setStyle("-fx-font-size: 14px;");
        algorithmLabel.setText(s.getAlgorithmString());
        algorithmLabel.setStyle("-fx-font-size: 14px;");
        criteriasLabel.setText(s.getCriterias());
        criteriasLabel.setStyle("-fx-font-size: 14px;");
        for (int j = 0; j < schedules.size(); j++) {
            Schedule newSchedule = schedules.get(j);
            for (int k = 0; k < newSchedule.getRuns(); k++) {
                System.out.println("added runs from runscount");
            queueSchedule.add(newSchedule);
           // newSchedule.getAlgorithm().sendListener(this);
            }
        }

    }

    public void stopEvolution() {
        stopGraphics();
        currentSchedule.getAlgorithm().stop();
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
        currentSchedule.getAlgorithm().stop();


        isRunning = false; // Set running state to false to stop the algorithm
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        batchInfo.add(batchNumberLabel, 1, 0);
        batchInfo.add(timesRunLabel, 1, 1);
        batchInfo.add(dimensionLabel, 1, 2);
        statsBatch.add(maxIterationsLabels, 1, 0);
        statsBatch.add(averageIterationsLabel, 1, 1);


        statsBatch.add(minIterationsLabel,1,2);
        statsBatch.add(maxFuncEvalLabel,1,3);
        statsBatch.add(minFuncEvalLabel,1,4);
        statsBatch.add(maxFitnessLabel,1,5);
        statsBatch.add(averageFitnessLabel,1,6);
        statsBatch.add(minFitnessLabel,1,7);


        batchInfo.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14;");
        statsBatch.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14;");
        batchNumberLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14;");
        timesRunLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14;");
        dimensionLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14;");
        maxIterationsLabels.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14;");
        averageIterationsLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14;");
        minIterationsLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14;");
        maxFuncEvalLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14;");
        minFuncEvalLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14;");
        maxFitnessLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14;");
        averageFitnessLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14;");
        minFitnessLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14;");



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

        sliderSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (timeline != null) {
                timeline.stop(); // Stop the timeline to reset the key frame duration
                double speed = newValue.doubleValue();
                KeyFrame keyFrame = new KeyFrame(Duration.seconds(1 / speed), event -> {
                   // System.out.println("Keyframe 1 running");
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
        if (timeline == null) {
            timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            tspIntialize();
        }

        sliderSpeed.setBlockIncrement(1.0);
        sliderSpeed.setMax(10.0);
        sliderSpeed.setMin(0.1);
        sliderSpeed.setValue(1.0);
        sliderSpeed.setMajorTickUnit(2.0);


        Solution solution = new Solution((TSPParser) currentSchedule.getSearchSpace());
        firstSolution(solution);



        speed = sliderSpeed.getValue();


        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1 / speed), event -> {
            processQueue();
           //System.out.println("Keyframe 2 running");

        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
        currentSchedule.getAlgorithm().sendListener(this);

        // Run the algorithm in a background thread
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("Starting algorithm run...");
                currentSchedule.run();
                System.out.println("Algorithm run completed.");
                return null;
            }
        };
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();


        startButton.setDisable(true);
        pauseButton.setDisable(false);
        stopButton.setDisable(false);
    }

    @FXML
    private void pauseVisualization() {
        if (timeline != null) {
            currentSchedule.getAlgorithm().pause();
            timeline.pause();
            System.out.println("paused");
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
        edgesAdded = 0;
        edgesDeleted = 0;

        edgeMap.clear();
        tspVisualization.getChildren().clear();
        stackPane.getChildren().clear();
        flowPane.getChildren().clear();
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
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        /*

        Circle circle4 = new Circle(40,360, 3, Color.RED);
        tspVisualization.getChildren().add(circle4);
        Circle circle1 = new Circle(40,18, 3, Color.RED);
        tspVisualization.getChildren().add(circle1);
        Circle circle2 = new Circle(590,360, 3, Color.RED);
        tspVisualization.getChildren().add(circle2);
        Circle circle3 = new Circle(590,18, 3, Color.RED);
        tspVisualization.getChildren().add(circle3);
*/
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
    }
    private void updateVisualization() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected
            Void call() throws Exception {

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
            Platform.runLater(() -> tspVisualization.getChildren().remove(line1));

            edgeMap.remove(edge1);
            edgesDeleted++;
        }

        if (line2 != null) {
            Platform.runLater(() -> tspVisualization.getChildren().remove(line2));

            edgeMap.remove(edge2);
            edgesDeleted++;
        }
        if (line3 != null) {
            Platform.runLater(() -> tspVisualization.getChildren().remove(line3));

            edgeMap.remove(edge3);
            edgesDeleted++;
        }
        if (line4 != null) {
            Platform.runLater(() -> tspVisualization.getChildren().remove(line4));
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

            Edge edge5 = new Edge(x5, y5, x6, y6);
            Edge edge6 = new Edge(x6, y6, x5, y5);
            Line line5 = edgeMap.get(edge5);
            Line line6 = edgeMap.get(edge6);

            if (line5 != null) {
                Platform.runLater(() -> tspVisualization.getChildren().remove(line5));


                edgeMap.remove(new Edge(x5, y5, x6, y6));
                edgesDeleted++;
            }
            if (line6 != null) {
                Platform.runLater(() -> tspVisualization.getChildren().remove(line6));

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
                    /*
                    System.out.println("Case 1");
                    System.out.println("x1: " + x1 + " y1: " + y1 + " x5: " + x5 + " y5: " + y5);
                    System.out.println("x2: " + x2 + " y2: " + y2 + " x6: " + x6 + " y6: " + y6);
                    System.out.println("x3: " + x3 + " y3: " + y3 + " x4: " + x4 + " y4: " + y4);

                     */

                    break;
                case 2:
                    // j -> k and j + 1 -> k + 1 and remain i-> i +1
                    newEdges.add(new Edge(x3, y3, x5, y5));
                    newEdges.add(new Edge(x4, y4, x6, y6));
                    newEdges.add(new Edge(x1, y1, x2, y2));
                    /*
                    System.out.println("Case 2");
                    System.out.println("x3: " + x3 + " y3: " + y3 + " x5: " + x5 + " y5: " + y5);
                    System.out.println("x4: " + x4 + " y4: " + y4 + " x6: " + x6 + " y6: " + y6);
                    System.out.println("x1: " + x1 + " y1: " + y1 + " x2: " + x2 + " y2: " + y2);

                     */
                    break;
                case 3:
                    // i -> j and i + 1 -> j +1 and remain k -> k +1
                    newEdges.add(new Edge(x1, y1, x3, y3));
                    newEdges.add(new Edge(x2, y2, x4, y4));
                    newEdges.add(new Edge(x5, y5, x6, y6));
                    /*
                    System.out.println("Case 3");
                    System.out.println("x1: " + x1 + " y1: " + y1 + " x3: " + x3 + " y3: " + y3);
                    System.out.println("x2: " + x2 + " y2: " + y2 + " x4: " + x4 + " y4: " + y4);
                    System.out.println("x5: " + x5 + " y5: " + y5 + " x6: " + x6 + " y6: " + y6);
                    */

                    break;
                case 4:
                    // i -> j and i + 1 -> k
                    // k +1 -> j +1
                    newEdges.add(new Edge(x1, y1, x3, y3));
                    newEdges.add(new Edge(x2, y2, x5, y5));
                    newEdges.add(new Edge(x6, y6, x4, y4));
                    /*
                    System.out.println("Case 4");
                    System.out.println("x1: " + x1 + " y1: " + y1 + " x3: " + x3 + " y3: " + y3);
                    System.out.println("x2: " + x2 + " y2: " + y2 + " x5: " + x5 + " y5: " + y5);
                    System.out.println("x6: " + x6 + " y6: " + y6 + " x4: " + x4 + " y4: " + y4);

                     */
                    break;
                case 5:
                    newEdges.add(new Edge(x1, y1, x5, y5));
                    newEdges.add(new Edge(x2, y2, x4, y4));
                    newEdges.add(new Edge(x3, y3, x6, y6));
                    /*
                    System.out.println("Case 5");
                    System.out.println("x1: " + x1 + " y1: " + y1 + " x5: " + x5 + " y5: " + y5);
                    System.out.println("x2: " + x2 + " y2: " + y2 + " x4: " + x4 + " y4: " + y4);
                    System.out.println("x3: " + x3 + " y3: " + y3 + " x6: " + x6 + " y6: " + y6);

                     */
                    break;
                case 6:
                    newEdges.add(new Edge(x1, y1, x4, y4));
                    newEdges.add(new Edge(x2, y2, x6, y6));
                    newEdges.add(new Edge(x3, y3, x5, y5));
                    /*
                    System.out.println("Case 6");
                    System.out.println("x1: " + x1 + " y1: " + y1 + " x4: " + x4 + " y4: " + y4);
                    System.out.println("x2: " + x2 + " y2: " + y2 + " x6: " + x6 + " y6: " + y6);
                    System.out.println("x3: " + x3 + " y3: " + y3 + " x5: " + x5 + " y5: " + y5);

                     */

                    break;
                case 7:
                    newEdges.add(new Edge(x1, y1, x4, y4));
                    newEdges.add(new Edge(x2, y2, x5, y5));
                    newEdges.add(new Edge(x3, y3, x6, y6));
                    /*
                    System.out.println("Case 7");
                    System.out.println("x1: " + x1 + " y1: " + y1 + " x4: " + x4 + " y4: " + y4);
                    System.out.println("x2: " + x2 + " y2: " + y2 + " x5: " + x5 + " y5: " + y5);
                    System.out.println("x3: " + x3 + " y3: " + y3 + " x6: " + x6 + " y6: " + y6);

                     */
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
            Platform.runLater(() -> { tspVisualization.getChildren().add(newLine1);} );
            Platform.runLater(() -> { tspVisualization.getChildren().add(newLine2);} );
            edgeMap.put(new Edge(x1, y1, x3, y3), newLine1);
            edgeMap.put(new Edge(x2, y2, x4, y4), newLine2);
        }
                Platform.runLater(() -> {
        for (Edge edge : newEdges) {
            Line newLine = new Line(xPush + edge.x1 / xScaling, edge.y1 / yScaling, xPush +edge.x2 / xScaling, edge.y2 / yScaling);
            newLine.setStroke(Color.GREEN);
            tspVisualization.getChildren().add(newLine);
            edgeMap.put(edge, newLine);
            edgesAdded++;
        }



            });

            return null;
        }
    };

    // Run the task in a background thread
    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();
}
    private void printEdgeMapDetails() {
        System.out.println("Current edges in edgeMap:");
        for (Map.Entry<Edge, Line> entry : edgeMap.entrySet()) {
            Edge edge = entry.getKey();
            Line line = entry.getValue();
            System.out.println("Edge: (" + edge.x1 + ", " + edge.y1 + ") -> (" + edge.x2 + ", " + edge.y2 + ")");
        }
    }

    @Override
    public void tspGraphics(ArrayList<TSPDATA> solution) {
        allSolutions = solution;
    }

    @Override
    public void receiveUpdate(TSPDATA solution){
        System.out.println("Added solution");
        updateQueue.add(solution);
    }
    boolean firstTime = true;
    boolean ACO = true;

    private void processQueue() {

        if (!updateQueue.isEmpty()) {
            TSPDATA nextSolution = updateQueue.poll();
            Platform.runLater(() -> {
            System.out.println("Next data: " + nextSolution.getGeneration());
            tableIterations.setCellValueFactory(new PropertyValueFactory<>("iteration"));
            tableFitness.setCellValueFactory(new PropertyValueFactory<>("fitness"));
            tableFuncEval.setCellValueFactory(new PropertyValueFactory<>("funcEval"));
            tableOptimalFitness.setCellValueFactory(new PropertyValueFactory<>("optimalFitness"));
            tableRuntime.setCellValueFactory(new PropertyValueFactory<>("runtime"));

            RowData rowData = new RowData(
                    Integer.toString(nextSolution.getGeneration()),
                    Integer.toString(nextSolution.getFitness()),
                    Integer.toString(nextSolution.getFunctionEvaluations()),
                    Integer.toString(nextSolution.getOptimum()),
                    Long.toString(nextSolution.getTimeElapsed())
            );

            ObservableList<RowData> data = FXCollections.observableArrayList();
            data.add(rowData);

            extractKeyFeaturesTable.setItems(data);
            extractKeyFeaturesTable.refresh();


            Platform.runLater(() -> {
                setSolution(nextSolution);
                //updateVisualization();
            });
            setSolution(nextSolution);
            if(nextSolution.getName() == "ACO" || nextSolution.getName() == "(u+y)EA" || nextSolution.getName() == "1+1EA" || nextSolution.getName() == "SA"){
                Platform.runLater(() -> {
                deleteAndDraw(nextSolution.getSolution());
                });
            }//else if (nextSolution.getName() == "1+1EA"){
              //  updateVisualization();
            //}
            if (nextSolution.isStopped()) {
                timeline.stop();
                pauseButton.setDisable(true);
                stopButton.setDisable(true);
                if (firstTime) {
                    updateUIPostAlgorithm(currentSchedule);
                }
                firstTime = false;
                System.out.println("stopped it");
                if (nextSolution.generation >= 9999999){
                    Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setContentText("Optimum not reached");
                    alert.showAndWait();
                    });
                }
            }
            });


        }

    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void startVisualizationBitString() {
        if (isPaused) {
            timeline.play();
            isPaused = false;
            pauseButton.setDisable(false);
            startButton.setDisable(true);
            return;
        }




        // Check if the timeline has already been created
        if (timeline == null) {
            timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);

            KeyFrame keyFrame1 = new KeyFrame(Duration.seconds(1 / speed), event -> {
                processBitStringQueue();
            });

            timeline.getKeyFrames().add(keyFrame1);

            sliderSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
                speed = newValue.doubleValue();
                timeline.getKeyFrames().clear();
                KeyFrame newKeyFrame = new KeyFrame(Duration.seconds(1 / speed), event -> {
                    processBitStringQueue();
                });
                timeline.getKeyFrames().add(newKeyFrame);
                if (!isPaused) {
                    timeline.playFromStart();
                }
            });
        }
        sliderSpeed.setBlockIncrement(50.0);
        sliderSpeed.setMax(500);
        sliderSpeed.setMin(0.1);
        sliderSpeed.setValue(50);
        sliderSpeed.setMajorTickUnit(100.0);

        speed = sliderSpeed.getValue();
        timeline.play();
        currentSchedule.getAlgorithm().sendListener(this);



        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                currentSchedule.run();
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();



        startButton.setDisable(true);
        pauseButton.setDisable(false);
        stopButton.setDisable(false);

    }
    private final Queue<Data> updateBitStringQueue = new LinkedList<Data>();
    @Override
    public void receiveBitstringUpdate(Data data) {
        updateBitStringQueue.add(data);
        System.out.println("Added data");
    }

    private void processBitStringQueue() {
        done = false;
        if (!updateBitStringQueue.isEmpty()) {
            Data nextData = updateBitStringQueue.poll();
            System.out.println("Next data: " + nextData.getGeneration());
            tableIterations.setCellValueFactory(new PropertyValueFactory<>("iteration"));
            tableFitness.setCellValueFactory(new PropertyValueFactory<>("fitness"));
            tableFuncEval.setCellValueFactory(new PropertyValueFactory<>("funcEval"));
            tableOptimalFitness.setCellValueFactory(new PropertyValueFactory<>("optimalFitness"));
            tableRuntime.setCellValueFactory(new PropertyValueFactory<>("runtime"));

            RowData rowData = new RowData(
                    Integer.toString(nextData.getGeneration()),
                    Integer.toString(nextData.getFitness()),
                    Integer.toString(nextData.getFunctionEvaluations()),
                    Integer.toString(nextData.getBitString().length()),
                    Long.toString(nextData.getTimeElapsed())
            );

            // add a value to tablefitness
            String fitness1 = Integer.toString(nextData.getFitness());
            //ArrayList<String> data = new ArrayList<>();


            ObservableList<RowData> data = FXCollections.observableArrayList();
            data.add(rowData);

            extractKeyFeaturesTable.setItems(data);
            extractKeyFeaturesTable.refresh();



            if(nextData.getImproved()) {
                runGraphics2(nextData);
            }
            if(nextData.isStop()) {
                done = true;
                timeline.stop();
                pauseButton.setDisable(true);
                stopButton.setDisable(true);
                updateUIPostAlgorithm(currentSchedule);
                System.out.println("Stopped succesfully");
            }
        }
    }
    public class RowData {
        private final SimpleStringProperty iteration;
        private final SimpleStringProperty fitness;
        private final SimpleStringProperty funcEval;
        private final SimpleStringProperty optimalFitness;
        private final SimpleStringProperty runtime;

        public RowData(String iteration, String fitness, String funcEval, String optimalFitness, String runtime) {
            this.iteration = new SimpleStringProperty(iteration);
            this.fitness = new SimpleStringProperty(fitness);
            this.funcEval = new SimpleStringProperty(funcEval);
            this.optimalFitness = new SimpleStringProperty(optimalFitness);
            this.runtime = new SimpleStringProperty(runtime);
        }

        public String getIteration() {
            return iteration.get();
        }

        public String getFitness() {
            return fitness.get();
        }

        public String getFuncEval() {
            return funcEval.get();
        }

        public String getOptimalFitness() {
            return optimalFitness.get();
        }

        public String getRuntime() {
            return runtime.get();
        }
    }
    boolean done = false;
    public void runGraphics2(Data data) {

        int generation = data.getGeneration();
        String bitString = data.getBitString();


        System.out.println("Generation: " + generation);
        int fitness = data.getFitness();
        Optional<Double> temp = data.getTemp();
        /*
        generationSlider.setBlockIncrement(10);
        generationSlider.setMajorTickUnit(50);
        generationSlider.setSnapToTicks(true);
        generationSlider.adjustValue(i);
        */
            // Create a Task for the background processing
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                if (isHypercubeSelected()) {
                    Circle circle = booleanHypercubeVisualization.getDisplayCoordinates(bitString, false);
                    Platform.runLater(() -> {
                        if (circle != null) {
                            if (lastCircle != null) {
                                booleanHypercubeVisualization.hypercubePane.getChildren().remove(lastCircle);
                            }
                            if (done) {
                                Circle perfectCircle = booleanHypercubeVisualization.getDisplayCoordinates(bitString, true);
                                booleanHypercubeVisualization.hypercubePane.getChildren().add(perfectCircle);
                            } else {
                                lastCircle = circle;
                                booleanHypercubeVisualization.hypercubePane.getChildren().add(circle);
                            }
                        }
                    });
                }

                if (isTextSelected()) {
                    Platform.runLater(() -> {
                        if (generation == 0) {
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
                    });
                }

                if (isGraphSelected()) {
                    Platform.runLater(() -> {
                        series.getData().add(new XYChart.Data<>(generation, fitness));
                    });
                }

                if (done) {
                    Platform.runLater(() -> {
                        String finalText = "Perfect solution found in generation " + generation + "\n";
                        solutionArea.appendText(finalText);
                    });
                }
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
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





