package group.ea;

import group.ea.controllers.Schedule;
import group.ea.structure.algorithm.Algorithm;
import group.ea.structure.algorithm.RLS;
import group.ea.structure.problem.OneMax;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.BitString;
import group.ea.structure.searchspace.SearchSpace;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
// make a forloop

public class main extends Application {
    double x,y = 0;
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(main.class.getResource("fxml/homePage.fxml")));
        Scene scene = new Scene(root);
        //stage.initStyle(StageStyle.UNDECORATED); // no border

        Platform.runLater(root::requestFocus); // don't focus any element initially

        root.setOnMousePressed( event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - x);
            stage.setY(event.getScreenY() - y);
        });


        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        //runExperiment();
        runSingle();
        //launch(args);

    }
    public static void runSingle(){
        int totalFitness = 0;
        for(int i = 0; i < 200; i++) {
            Schedule newSchedule = new Schedule();
            newSchedule.setSearchSpaceString("Permutations");
            newSchedule.setProblemString("TSP");
            newSchedule.setAlgorithmString("Permutation1+1EA");
            newSchedule.setIterationBound(10000);
            newSchedule.setUpAlgorithm();
            newSchedule.getAlgorithm().runAlgorithm();
            totalFitness = totalFitness + newSchedule.getAlgorithm().getGeneration();


        }
        System.out.println("Average fitness: " + totalFitness/200);
    }
    public static void runExperiment(){
        int[] bitStringLengths = {10, 20, 50, 100, 200, 500, 1000,2000}; // Example lengths
        int runsPerObservation = 200;

        List<DataPoint> dataPoints = new ArrayList<>();

        for (int length : bitStringLengths) {
            int totalIterations = 0;

            for (int run = 0; run < runsPerObservation; run++) {
                Schedule newSchedule = new Schedule();
                newSchedule.setSearchSpaceString("Permutations");
                newSchedule.setProblemString("TSP");
                newSchedule.setAlgorithmString("Permutation1+1EA");
                newSchedule.setIterationBound(10000000);
                newSchedule.setUpAlgorithm();
                newSchedule.getAlgorithm().runAlgorithm();
                int iterations = newSchedule.getAlgorithm().getGeneration();
                totalIterations = totalIterations + iterations;
            }
            dataPoints.add(new DataPoint(length, totalIterations/runsPerObservation));

        }
        System.out.println("Experiment done");
        saveDataToCSV("onemax_experiment.csv", dataPoints);
    }
    public static void saveDataToCSV(String filename, List<DataPoint> dataPoints) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.append("BitStringLength,Iterations\n");
            for (DataPoint dataPoint : dataPoints) {
                writer.append(dataPoint.getBitStringLength()).append(",")
                        .append(dataPoint.getIterations()).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }





/*
FXMLLoader fxmlLoader = new FXMLLoader(main.class.getResource("fxml/home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 740, 500);
        stage.setScene(scene);
        stage.show();
 */