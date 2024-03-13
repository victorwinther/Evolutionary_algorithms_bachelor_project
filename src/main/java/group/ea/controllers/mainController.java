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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class mainController {
    @FXML
    private Button btnPlot, btnConnect, btnTable, createBlueprintBtn, loadBlueprintBtn;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Label searchspaceLabel,problemLabel, algorithmLabel,criteriasLabel,timeLabel,mutationLabel, selectionLabel,crossoverLabel;

    @FXML
    Slider sliderSpeed;

    private static AnimationTimer animationTimer;

    Algorithm algorithm;

    public volatile boolean isRunning = false;

    private Stage stage;
    private Scene scene;
    private Parent parent;
    private FileChooser fileChooser = new FileChooser();
    private String[] blueprintChoices;
    double duration = 1000;

    public mainController() {
       animationTimer = new AnimationTimer() {
           private long lastUpdate = 0;
           @Override
           public void handle(long l) {
               //algorithm.performSingleUpdate();
               if (l - lastUpdate >= duration) { // Update every second
                   algorithm.updateGraphics();
                   lastUpdate = l;
                    double speed = sliderSpeed.getValue();
                    duration =  (TimeUnit.MILLISECONDS.toNanos(1000) * (1 - speed / sliderSpeed.getMax()));
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
    public TextArea solutionArea;

    private int stringLength = 100; // Length of the binary string

    // Button action to start the EA
    @FXML
    private void startEvolution() {
        isRunning = true;
        //startAlgorithm();
        solutionArea.clear(); // Clear previous solutions
        //new Thread(this::runEvolution).start(); // Run EA in a separate thread
        SearchSpace searchSpace = null;
        if ("Bit strings".equals(blueprintChoices[0])) {
            searchSpace = new BitString(30);
            System.out.println("is here");
        } else if ("Permutation".equals(blueprintChoices[0])) {
            //searchSpace = new Permutation(100);
        }

        Problem problem = null;
        if("OneMax".equals(blueprintChoices[1])){
            problem = new OneMax(searchSpace);
        }
        else if("LeadingOnes".equals(blueprintChoices[1])){
            problem = new LeadingOnes(searchSpace);
        }

        if(blueprintChoices[2].equals("RLS")){
            algorithm = new RLS(searchSpace, problem, this);
        }
        else if(blueprintChoices[2].equals("Simulated Annealing")){
            algorithm = new SA(searchSpace, problem, this);
        } else{
            algorithm = null;
        }
        if(algorithm != null){
            //new Thread(() -> algorithm.runAlgorithm()).start();
            //algorithm.initialize();
            startAlgorithm();
            algorithm.runAlgorithm();
        }
        //new Thread(() -> algorithm.runAlgorithm()).start();
        // Running the algorithm
       // onePlusOneEA.runAlgorithm();
        //new Thread(this::runEvolution).start();
    }
    public void recieveArray(String[] blueprintChoices) {
        this.blueprintChoices = blueprintChoices;
        searchspaceLabel.setText(blueprintChoices[0]);
        problemLabel.setText(blueprintChoices[1]);
        algorithmLabel.setText(blueprintChoices[2]);
        criteriasLabel.setText(blueprintChoices[3]);
    }
    public void stopEvolution() {
        stopAlgorithm();
        isRunning = false; // Set running state to false to stop the algorithm
        // Optionally stop the AnimationTimer if needed
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

    public void stopAlgorithm() {
        animationTimer.stop(); // Stop the animation
    }

}