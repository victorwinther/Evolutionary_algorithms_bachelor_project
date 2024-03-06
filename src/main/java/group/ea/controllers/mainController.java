package group.ea.controllers;

import group.ea.main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.io.IOException;
import java.util.Objects;

public class mainController {
    @FXML
    private Button btnPlot, btnConnect, btnTable, createBlueprintBtn, loadBlueprintBtn;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Label problemLabel, algorithmLabel, selectionLabel,crossoverLabel, mutationLabel;

    private Stage stage;
    private Scene scene;
    private Parent parent;
    private FileChooser fileChooser = new FileChooser();


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

     void setLabels(){
        blueprintController blueprintController = new blueprintController();
        problemLabel.setText(blueprintController.getSearchspaceSelector());
        algorithmLabel.setText(blueprintController.getAlgorithmSelector());
        selectionLabel.setText("Selection: Random");
        crossoverLabel.setText("Crossover: N/A");
        mutationLabel.setText("Mutation: Bit flip");
    }
    @FXML
    private Button startButton;
    @FXML
    private TextArea solutionArea;

    private int stringLength = 100; // Length of the binary string

    // Button action to start the EA
    @FXML
    private void startEvolution() {
        solutionArea.clear(); // Clear previous solutions
        new Thread(this::runEvolutionSA).start(); // Run EA in a separate thread
    }

    // EA logic adapted from OnePlusOneEAOneMax
    private void runEvolution() {
        String parent = initializeIndividual(stringLength);
        int bestFitness = fitness(parent);

        String finalParent = parent;
        int finalBestFitness = bestFitness;
        Platform.runLater(() -> solutionArea.appendText("Initial Solution: " + finalParent + " with fitness: " + finalBestFitness + "\n"));

        int maxGenerations = 500;
        for (int generation = 1; generation <= maxGenerations; generation++) {
            String offspring = mutate(parent);
            int offspringFitness = fitness(offspring);

            if (offspringFitness > bestFitness) {
                parent = offspring;
                bestFitness = offspringFitness;
                String solutionText = "Generation " + generation + ": New solution found: " + parent + " with fitness: " + bestFitness + "\n";
                Platform.runLater(() -> solutionArea.appendText(solutionText));
            }

            if (bestFitness == stringLength) {
                int finalGeneration = generation;
                Platform.runLater(() -> solutionArea.appendText("Perfect solution found in generation " + finalGeneration + "\n"));
                break;
            }
        }
    }




    private void runEvolutionSA(){
        double initTemp = 2;
        double tempReduction = 0.99;
        String parent = initializeIndividual(stringLength);
        int bestFitness = fitness(parent);

        String finalParent = parent;
        int finalBestFitness = bestFitness;
        Platform.runLater(() -> solutionArea.appendText("Initial Solution: " + finalParent + " with fitness: " + finalBestFitness + "\n"));

        double currentTemp = initTemp;
        int maxGen = 500;
        int currentGen = 0;
        while(currentTemp > 0.00001 && currentGen < maxGen) {

            String offspring = mutate(parent);
            int offspringFitness = fitness(offspring);

            if (offspringFitness > bestFitness) {
                parent = offspring;
                bestFitness = offspringFitness;
                String solutionText = "Generation " + currentGen + ": New solution found: " + parent + " with fitness: " + bestFitness + " tempature is " + currentTemp + "\n";
                Platform.runLater(() -> solutionArea.appendText(solutionText));
            }
            else if ( offspringFitness == bestFitness) {
                parent = offspring;
            }
            else {
                double SARate = Math.random();
                double SAEnergy = Math.exp((offspringFitness - bestFitness) / currentTemp);

                if(SAEnergy > SARate){
                    parent = offspring;
                    bestFitness = offspringFitness;
                    String solutionText = "Generation " + currentGen + ": New SA found: " + parent + " with fitness: " + bestFitness + " tempature is " + currentTemp + "\n";
                    Platform.runLater(() -> solutionArea.appendText(solutionText));

                }
            }


            if (bestFitness == stringLength) {
                int finalGeneration = currentGen;
                Platform.runLater(() -> solutionArea.appendText("Perfect solution found in generation " + finalGeneration + "\n"));
                break;
            }
            currentTemp *= tempReduction;
            currentGen++;

        }
    }


    // Fitness function
    private int fitness(String individual) {
        int count = 0;
        for (int i = 0; i < individual.length(); i++) {
            if (individual.charAt(i) == '1') {
                count++;
            }
        }
        return count;
    }

    // Initialize a random binary string
    private String initializeIndividual(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(Math.random() > 0.5 ? '1' : '0');
        }
        return sb.toString();
    }

    // Mutation function: flips a random bit
    private String mutate(String parent) {
        int mutateIndex = (int) (Math.random() * parent.length());
        char[] chars = parent.toCharArray();
        chars[mutateIndex] = chars[mutateIndex] == '0' ? '1' : '0';
        return new String(chars);
    }
}